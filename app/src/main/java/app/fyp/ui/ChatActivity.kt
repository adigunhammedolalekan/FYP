package app.fyp.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.fyp.*
import app.fyp.models.Conversation
import app.fyp.models.Message
import app.fyp.vms.VM
import kotlinx.android.synthetic.main.layout_chat.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import org.parceler.Parcels

class ChatActivity: AppCompatActivity() {

    private val session by inject<Session>()
    private val vm by viewModel<VM>()
    private var conversation = Conversation()
    private val messages = ArrayList<Message>()
    private val adapter by lazy { ChatAdapter(session, messages) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_chat)

        val sIntent = intent
        if (sIntent == null) {
            finish()
            return
        }

        conversation = Parcels.unwrap<Conversation>(sIntent.getParcelableExtra(Conversation.KEY))
        supportActionBar?.title = "Chat with ${conversation.name(session.currentUser().userId)}"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupViews()
        vm.loadMessages(friendId())
        loadMessages()
        listenForMessages()
    }

    private fun setupViews() {
        btnSendMessage.setOnClickListener {
            val text = edtChatBox.text.trim().toString()
            if (text == "") return@setOnClickListener

            vm.sendMessage(text, friendId())
            edtChatBox.setText("")
            val newMessage = Message(text, session.currentUser().userId, friendId())
            messages.add(newMessage)

            adapter.notifyDataSetChanged()
            rvMessages.scrollToPosition(messages.size)
            hideKeyboard()
        }
        rvMessages.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false).apply {
            stackFromEnd = true
        }
        rvMessages.adapter = adapter
    }

    private fun loadMessages() {
        vm.messageListLiveData().observe(this, Observer {
            when(it.state) {
                State.SUCCESS -> {
                    L.fine("new message from observer")
                    val newMessages = it.data ?: return@Observer
                    messages.addAll(newMessages)
                    L.fine("Message size ${messages.size}")
                    adapter.notifyDataSetChanged()

                    rvMessages.scrollToPosition(messages.size)
                }
                State.LOADING -> {}
                State.ERROR -> {}
            }
        })
    }

    private fun listenForMessages() {
        vm.messagesLiveData().observe(this, Observer {
            when(it.state) {
                State.SUCCESS -> {
                    val newMessage = it.data ?: return@Observer
                    messages.add(newMessage)

                    adapter.notifyDataSetChanged()
                    rvMessages.scrollToPosition(messages.size)
                }
            }
        })
    }

    private fun friendId() = if (conversation.toId == session.currentUser().userId)
        conversation.fromId else conversation.toId

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }
}