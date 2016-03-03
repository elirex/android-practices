package elirexcom.dragdropsample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mChatListView, mToolListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mChatListView = (RecyclerView) findViewById(R.id.chat_list);
        mToolListView = (RecyclerView) findViewById(R.id.tool_list);

        // mChatListView.setOnDragListener(new ToolDragListener(mChatListView));
        // mToolListView.setOnDragListener(new ToolDragListener(mChatListView));

        mChatListView.setLayoutManager(new LinearLayoutManager(this));
        mToolListView.setLayoutManager(new GridLayoutManager(this, 5));

        List<Integer> toolList = new ArrayList<Integer>();
        toolList.add(R.mipmap.tool_buy_48dp);
        toolList.add(R.mipmap.tool_friend_48dp);
        toolList.add(R.mipmap.tool_map_48dp);

        List<String> chatList = new ArrayList<String>();
        for(int i = 0; i < 50; ++i) {
            chatList.add("Item " + (i+1));
        }

        ChatListAdapter chatListAdapter = new ChatListAdapter(chatList);
        ToolListAdapter toolListAdapter = new ToolListAdapter(toolList);

        mChatListView.setAdapter(chatListAdapter);
        mToolListView.setAdapter(toolListAdapter);

    }



}
