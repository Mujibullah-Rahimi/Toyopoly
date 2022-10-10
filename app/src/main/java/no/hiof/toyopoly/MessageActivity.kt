package no.hiof.toyopoly

import android.os.Bundle
import android.os.PersistableBundle
import com.sendbird.uikit.activities.ChannelListActivity
import com.sendbird.uikit.fragments.ChannelListFragment


class MessageActivity : ChannelListActivity() {
    private fun createChannelListFragment(): ChannelListFragment {
        return ChannelListFragment.Builder()
            .setUseHeaderRightButton(true)
            .setHeaderTitle("Messages")
            .setUseHeader(true)
            .setUseHeaderLeftButton(true)
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

        createChannelListFragment()
    }
}