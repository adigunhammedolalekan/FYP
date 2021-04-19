package app.fyp.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.fyp.R
import app.fyp.Session
import app.fyp.State
import app.fyp.models.Conversation
import app.fyp.vms.VM
import kotlinx.android.synthetic.main.layout_home.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import org.parceler.Parcels

class HomeActivity : AppCompatActivity() {

    private val session by inject<Session>()
    private val vm by viewModel<VM>()
    private val conversations = ArrayList<Conversation>()
    private val adapter by lazy { ConversationAdapter(conversations, session) {conversation ->
        val wrapped = Parcels.wrap(conversation)
        val chatIntent = Intent(this, ChatActivity::class.java).apply {
            putExtra(Conversation.KEY, wrapped)
        }
        startActivity(chatIntent)
    }}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_home)

        supportActionBar?.title = session.currentUser().name()
        setupViews()
        subscribe()
        vm.loadConversations()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun subscribe() {
        vm.conversationListLiveData().observe(this, Observer {
            when(it.state) {
                State.LOADING -> { pwHome.visibility = View.VISIBLE }
                State.ERROR -> {}
                State.SUCCESS -> {
                    val data = it.data ?: ArrayList()
                    if (data.size > 0)
                        conversations.clear()
                    for (conversation in data) {
                        conversations.add(conversation)
                    }
                    adapter.notifyDataSetChanged()
                }
            }
            if (it.state != State.LOADING)
                pwHome.visibility = View.GONE
        })
    }

    private fun setupViews() {
        rvHome.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        rvHome.adapter = adapter
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.actionAddFriend -> {
                startActivity(Intent(this, AddFriendActivity::class.java))
                true
            }
            R.id.logOut -> {
                session.destroy()
                val homePageIntent = Intent(this, MainActivity::class.java)
                    .apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                startActivity(homePageIntent)
                true
            } else ->  super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        vm.loadConversations()
    }
}