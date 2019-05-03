package com.comp3617.assignment2;

import android.icu.text.SimpleDateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.comp3617.assignment2.data.Task;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import io.realm.OrderedRealmCollection;
import io.realm.RealmBaseAdapter;

/**
 * Created by houstonkrohman on 2017-11-15.
 */

public class TaskAdapter extends RealmBaseAdapter<Task> implements ListAdapter{

    private List<Task>    taskList;

    private Set<Integer> countersToDelete = new HashSet<Integer>();

    //Constructor
    TaskAdapter(OrderedRealmCollection<Task> realmResults) {
        super(realmResults);
    }

    Set<Integer> getCountersToDelete() {
        return countersToDelete;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.task_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.taskData = (TextView) convertView.findViewById(R.id.description);
            viewHolder.dueDate = (TextView) convertView.findViewById(R.id.dueDate);
            viewHolder.alarmIcon = (ImageView) convertView.findViewById(R.id.imageView);
            viewHolder.priority = (TextView) convertView.findViewById(R.id.priority);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (adapterData != null) {
            final Task task = adapterData.get(position);

            //Populate UI component data
            viewHolder.taskData.setText(task.getTaskData());
            SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
            if(task.getDueDate() != null) {
                viewHolder.dueDate.setText(fmt.format(task.getDueDate()));
            }
            else {
                viewHolder.dueDate.setText(R.string.dueDateTxt);
            }

            switch (task.getPriority()){
                case 1:
                    viewHolder.priority.setText(R.string.hPriority);
                    break;
                case 2:
                    viewHolder.priority.setText(R.string.mPriority);
                    break;
                case 3:
                    viewHolder.priority.setText(R.string.lPriority);
                    break;
            }
            viewHolder.alarmIcon.setColorFilter(R.color.iconSet);

            if(!task.isReminderON()) {
                viewHolder.alarmIcon.setVisibility(View.GONE);
            }
        }
        return convertView;
    }

    private static class ViewHolder {
        TextView  taskData;
        TextView  dueDate;
        TextView  priority;
        ImageView alarmIcon;
    }


}





