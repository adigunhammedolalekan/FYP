package app.fyp.vms

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.fyp.L
import app.fyp.Resource
import app.fyp.Session
import app.fyp.Useful
import app.fyp.models.Conversation
import app.fyp.models.Message
import app.fyp.models.User
import com.google.firebase.database.*

class VM (private val session: Session,
          private val conversationRef: DatabaseReference,
          private val accountRef: DatabaseReference,
          private val messagesRef: DatabaseReference): ViewModel() {

    private val conversationsLiveData = MutableLiveData<Resource<ArrayList<Conversation>>>()
    private val addFriendLiveData = MutableLiveData<Resource<String>>()
    private val messagesLiveData = MutableLiveData<Resource<Message>>()
    private val messageListLiveData = MutableLiveData<Resource<ArrayList<Message>>>()
    private val currentUserId = session.currentUser().userId
    private var isFirstMessage = false

    fun loadConversations() {
        conversationsLiveData.postValue(Resource.loading())
        conversationRef.child(session.currentUser().userId).get().addOnCompleteListener {task ->
            if (task.isSuccessful) {
                val snapshot = task.result?.children ?: return@addOnCompleteListener
                val conversations = ArrayList<Conversation>()
                snapshot.forEach { dataSnapshot ->
                    val conversation = dataSnapshot.getValue(Conversation::class.java)
                    conversations.add(conversation ?: Conversation())
                }
                conversationsLiveData.postValue(Resource.success(conversations))
            }else {
                conversationsLiveData.postValue(Resource.error("failed to load conversations at this time. please retry"))
            }
        }
    }

    fun addFriend(message: String, email: String) {
        addFriendLiveData.postValue(Resource.loading())
        accountRef.child(Useful.cleanEmail(email))
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val snapshot = task.result
                    if (snapshot?.exists() == true) {
                        val account = snapshot.getValue(User::class.java) ?: User()
                        val currentUserId = session.currentUser().userId
                        val conversation = Conversation(account.userId, session.currentUser().userId,
                            account.name(), session.currentUser().name(), message)
                        storeConversation(currentUserId, account.userId, conversation)
                        storeConversation(account.userId, currentUserId, conversation)
                        addFriendLiveData.postValue(Resource.success("friend added successfully"))
                    }else {
                        addFriendLiveData.postValue(Resource.error("friend with email $email not found. please check the email address and try again"))
                    }
                }else {
                    addFriendLiveData.postValue(Resource.error("friend with email $email not found. please check the email address and try again"))
                }
            }
    }

    fun loadMessages(userId: String) {
        messagesRef.child(session.currentUser().userId).child(userId).orderByKey().get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val messages = ArrayList<Message>()
                val snapshots = task.result?.children ?: return@addOnCompleteListener
                snapshots.forEach { nextSnapshot ->
                    val message = nextSnapshot.getValue(Message::class.java) ?: return@addOnCompleteListener
                    messages.add(message)
                    messageListLiveData.postValue(Resource.success(messages))
                }
                listenForMessages(userId)
            }
        }
    }

    private fun listenForMessages(userId: String) {
        messagesRef.child(session.currentUser().userId).child(userId).addChildEventListener(object: ChildEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                // ignore first entry.
                if (!isFirstMessage) {
                    L.fine("onChildAdded: ignored")
                    isFirstMessage = true
                    return
                }
                val newMessage = snapshot.getValue(Message::class.java)
                if (newMessage?.fromId == currentUserId) return // it's a message from currently authenticated user.
                messagesLiveData.postValue(Resource.success(newMessage))
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }
        })
    }

    fun sendMessage(text: String, toUserId: String) {
        val message = Message(text, session.currentUser().userId, toUserId)
        val currentUserId = session.currentUser().userId
        messagesRef.child(currentUserId).child(toUserId).push().setValue(message)
        messagesRef.child(toUserId).child(currentUserId).push().setValue(message)

        updateConversation(currentUserId, toUserId, text)
        updateConversation(toUserId, currentUserId, text)
    }

    private fun updateConversation(forUser: String, toUser: String, newText: String) {
        val update: MutableMap<String, Any> = HashMap()
        update["lastMessage"] = newText
        conversationRef.child(forUser).child(toUser).updateChildren(update)
    }

    private fun storeConversation(forUser: String, toUser: String, value: Conversation) {
        conversationRef.child(forUser).child(toUser).setValue(value)
    }

    fun addFriendLiveData() = addFriendLiveData
    fun conversationListLiveData() = conversationsLiveData
    fun messagesLiveData() = messagesLiveData
    fun messageListLiveData() = messageListLiveData
}