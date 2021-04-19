package app.fyp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.fyp.R
import app.fyp.Session
import app.fyp.models.Message
import java.lang.IllegalArgumentException

class ChatAdapter(private val session: Session,
                  private val messages: ArrayList<Message>): RecyclerView.Adapter<ChatAdapter.ChatVH>() {

    companion object {
        const val CHAT_IN = 1
        const val CHAT_OUT = 2;
    }

    open class ChatVH(private val view: View): RecyclerView.ViewHolder(view) {}

    class InChatVH(private val view: View): ChatVH(view) {
        private val tvText = view.findViewById<TextView>(R.id.tvInText)
        fun bind(message: Message) {
            tvText.text = message.text
        }
    }

    class OutChatVH(private val view: View): ChatVH(view) {
        private val tvText = view.findViewById<TextView>(R.id.tvOutText)
        fun bind(message: Message) {
            tvText.text = message.text
        }
    }

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        return if (message.fromId == session.currentUser().userId)
            CHAT_OUT else CHAT_IN
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatVH {
        val ctx = parent.context
        val inflater = LayoutInflater.from(ctx)
        return when(viewType) {
            CHAT_IN -> InChatVH(inflater.inflate(R.layout.layout_message_in, parent, false))
            CHAT_OUT -> OutChatVH(inflater.inflate(R.layout.layout_message_out, parent, false))
            else -> throw IllegalArgumentException("unknown viewType $viewType")
        }
    }

    override fun getItemCount() = messages.size

    override fun onBindViewHolder(holder: ChatVH, position: Int) {
        val message = messages[position]
        when(holder) {
            is InChatVH -> holder.bind(message)
            is OutChatVH -> holder.bind(message)
            else -> throw IllegalArgumentException("unknown viewHolder")
        }
    }
}