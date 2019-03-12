package com.example.mininote;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.List;
import java.util.UUID;

public class NotePagerActivity extends AppCompatActivity {
    private static final String EXTRA_NOTE_ID =
            "com.example.mininote.note_id";

    private ViewPager mViewPager;
    private List<Note> mNotes;

    public static Intent newIntent(Context packageContext , UUID noteId){
        Intent intent = new Intent(packageContext,NotePagerActivity.class);
        intent.putExtra(EXTRA_NOTE_ID,noteId);
        return intent;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_pager);

        UUID noteId = (UUID) getIntent().getSerializableExtra(EXTRA_NOTE_ID);

        mViewPager = findViewById(R.id.note_viewpager);
        mNotes = NotesLab.getLab(this).getNotes();  //list作为数据源

        FragmentManager fragmentManager = getSupportFragmentManager();

        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {//FragmentPagerAdapter()是另一个可以使用的Adapter
            //两者的区别在于,卸载不需要的fragment时各自采用的处理方法有所不同
            //FragmentStatePagerAdapter()会销毁不需要的fragment,事务提交后activity的FragmentManager中
            //的该fragment会被彻底删除,state的意思是销毁时可在onSaveInstanceState(Bundle)中保存fragment的Bundle
            //信息,用户切换回来时可以通过保存的状态生成新的fragment
            //相比之下FragmentPagerAdapter有不同的做法,对于不在需要的fragment,FragmentManager会选择调用事务的
            //Detach方法来处理它,而非remove(Fragment)方法,也就是说,FragmentPagerAdapter只销毁了fragment的视图
            //而fragment实例还会保留在FragmentManager中,因此FragmentPagerAdapter创建的fragment永远不会销毁
            //对于少页面的应用来说FragmentPagerAdapter()更加适合
            @Override
            public Fragment getItem(int i) {
                Note note = mNotes.get(i);
                return NoteFragment.newInstance(note.getId());
            }

            @Override
            public int getCount() {
                return mNotes.size();
            }
        });
        for (int i = 0; i < mNotes.size(); i++) {
            if (mNotes.get(i).getId().equals(noteId)){
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }
}
