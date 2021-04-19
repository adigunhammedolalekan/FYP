package app.fyp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.fyp.R
import app.fyp.Session
import app.fyp.models.Conversation
import kotlinx.android.synthetic.main.layout_conversation.view.*

open class ConversationAdapter(private val conversations: ArrayList<Conversation>,
                          private val session: Session,
                          private val clickListener: (Conversation) -> Unit):
        RecyclerView.Adapter<ConversationAdapter.ConversationVH>() {

    inner class ConversationVH(private val view: View): RecyclerView.ViewHolder(view) {
        val conversationName = view.findViewById<TextView>(R.id.tvConversationName)
        val root = view.findViewById<RelativeLayout>(R.id.conversationRoot)
        val lastMessage = view.findViewById<TextView>(R.id.tvConversationLastMessage)

        fun bind(conversation: Conversation) {
            val idx = if (conversation.lastMessage.length > 20)
                20 else conversation.lastMessage.length
            conversationName.text = conversation.name(session.currentUser().userId)
            val lastText = if (idx > 20) conversation.lastMessage.substring(0, idx) + "..." else conversation.lastMessage.substring(0, idx)
            lastMessage.text = lastText

            root.setOnClickListener { clickListener(conversation) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationVH {
        val inflater = LayoutInflater.from(parent.context)
        return ConversationVH(inflater.inflate(R.layout.layout_conversation, parent, false))
    }

    override fun getItemCount() = conversations.size

    override fun onBindViewHolder(holder: ConversationVH, position: Int) {
        val conversation = conversations[position]
        holder.bind(conversation)
    }
}