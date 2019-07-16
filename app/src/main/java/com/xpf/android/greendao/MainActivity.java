package com.xpf.android.greendao;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xpf.android.greendao.bean.DaoSession;
import com.xpf.android.greendao.bean.Student;
import com.xpf.android.greendao.bean.StudentDao;

import org.greenrobot.greendao.query.DeleteQuery;
import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.query.WhereCondition;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.btnInsert)
    Button btnInsert;
    @BindView(R.id.btnDelete)
    Button btnDelete;
    @BindView(R.id.btnUpdate)
    Button btnUpdate;
    @BindView(R.id.btnQuery)
    Button btnQuery;
    private DaoSession mDaoSession;
    private MyAdapter myAdapter;
    private List<Student> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mDaoSession = ((MyApplication) getApplication()).getDaoSession();
        initData();
    }

    private void initData() {
        list = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            String message = "第" + i + "个数据";
            String name = "第" + i + "个名字";
            Student student = new Student(name, message, System.currentTimeMillis() - 60 * 1000 * i);
            list.add(student);
        }

        setAdapter();
    }

    private void setAdapter() {
        if (myAdapter == null) {
            myAdapter = new MyAdapter(list);
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(myAdapter);
        myAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Toast.makeText(MainActivity.this, "position:" + position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void refreshAdapter(List<Student> students) {
        list.clear();
        if (students != null && students.size() > 0) {
            list.addAll(students);
        }
        if (myAdapter != null) {
            myAdapter.notifyDataSetChanged();
        }
    }

    @OnClick({R.id.btnInsert, R.id.btnDelete, R.id.btnUpdate, R.id.btnQuery})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnInsert:
                insert();
                break;
            case R.id.btnDelete:
                delete();
                break;
            case R.id.btnUpdate:
                update();
                break;
            case R.id.btnQuery:
                List<Student> list = queryAll();
                refreshAdapter(list);
                break;
        }
    }

    public void insert() {
        mDaoSession.getStudentDao().insertInTx(list);
    }

    private void delete() {
        mDaoSession.getStudentDao().deleteAll();
    }

    private void update() {
        //mDaoSession.getStudentDao().update();
    }

    private void queryById(Long id) {
        mDaoSession.getStudentDao().queryBuilder();
        List<Student> list = mDaoSession.queryRaw(Student.class, "where id = ?", String.valueOf(id));
        refreshAdapter(list);
    }

    public List<Student> queryAll() {
        return mDaoSession.loadAll(Student.class);
    }

    public List quetyList(String message) {
        QueryBuilder<Student> qb = mDaoSession.queryBuilder(Student.class);
        // 查出所有的数据
        List<Student> list = qb.list();

        //查出当前对应message的数据
        QueryBuilder<Student> thingQueryBuilder = qb.where(StudentDao.Properties.Message.like(message)).orderAsc(StudentDao.Properties.Message);
        List<Student> thingList = thingQueryBuilder.list();

        //查询Message值为message时，按Name值排序的结果
        qb = mDaoSession.queryBuilder(Student.class);
        List<Student> list1 = qb.where(StudentDao.Properties.Message.eq(message)).orderAsc(StudentDao.Properties.Name).list();

        //嵌套查询： 查询Id大于5小于5，且Message值为message的数据
        qb = mDaoSession.queryBuilder(Student.class);
        List<Student> list2 = qb.where(StudentDao.Properties.Message.eq(message),
                qb.and(StudentDao.Properties.Id.gt(5),
                        StudentDao.Properties.Id.le(50))).list();

        return list;
    }

    public List queryList() {
        QueryBuilder<Student> qb = mDaoSession.queryBuilder(Student.class);
        // 嵌套查询： 查询Id大于5小于5，且Message值为message的数据
        return qb.where(StudentDao.Properties.Message.eq("一"),
                qb.and(StudentDao.Properties.Id.gt(5),
                        StudentDao.Properties.Id.le(50))).list();
    }

    public List queryListByOther() {
        QueryBuilder<Student> qb = mDaoSession.queryBuilder(Student.class);
        // 搜索条件为Id值大于1，即结果为[2,3,4,5,6,7,8,9,10,11];
        // offset(2)表示往后偏移2个，结果为[4,5,6,7,8,9,10,11,12,13];
        return qb.where(StudentDao.Properties.Id.gt(1)).limit(10).offset(2).list();
    }

    public List queryListByMoreTime() {
        QueryBuilder<Student> qb = mDaoSession.queryBuilder(Student.class);
        //搜索条件为Id值大于1，即结果为[2,3,4,5,6,7,8,9,10,11];
        // offset(2)表示往后偏移2个，结果为[4,5,6,7,8,9,10,11,12,13];
        Query<Student> query = qb.where(StudentDao.Properties.Id.gt(1)).limit(10).offset(2).build();
        List<Student> list = query.list();
        //通过SetParameter来修改上面的查询条件，比如我们将上面条件修改取10条Id值大于5，往后偏移两位的数据，方法如下！
        query.setParameter(0, 5);
        return query.list();
    }

    public List queryListBySqL() {
        Query<Student> query = mDaoSession
                .queryBuilder(Student.class)
                .where(new WhereCondition.StringCondition("_ID IN " + "(SELECT _ID FROM THING WHERE _ID > 5)"))
                .build();

        return query.list();
    }

    public boolean deleteItem() {
        QueryBuilder<Student> where = mDaoSession
                .queryBuilder(Student.class)
                .where(StudentDao.Properties.Id.gt(5));

        DeleteQuery<Student> deleteQuery = where.buildDelete();
        deleteQuery.executeDeleteWithoutDetachingEntities();

        return false;
    }


    /**
     * 批量更新
     */
    public void updateListInTx() {
        List<Student> things = mDaoSession.getStudentDao().loadAll();
        for (Student thing : things) {
            thing.setMessage(thing.getMessage() + " GoodBye 你了");
        }
        mDaoSession.getStudentDao().updateInTx(things);
        refreshAdapter(null);
    }

    public void insertListInTx(List<Student> thingList) {
        mDaoSession.getStudentDao().insertInTx(thingList);
        refreshAdapter(null);
    }

    public void deleteListInTx(List<Student> thingList) {
        mDaoSession.getStudentDao().deleteInTx(thingList);
        refreshAdapter(null);
    }

    class MyAdapter extends BaseQuickAdapter<Student, BaseViewHolder> {

        public MyAdapter(@Nullable List<Student> data) {
            super(R.layout.item_student, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, Student item) {
            helper.setText(R.id.textView, item.getName() + "，" + item.getMessage());
        }
    }
}
