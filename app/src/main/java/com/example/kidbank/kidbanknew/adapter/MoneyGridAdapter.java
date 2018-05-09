package com.example.kidbank.kidbanknew.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.kidbank.kidbanknew.Network.CommanClass;
import com.example.kidbank.kidbanknew.R;

/**
 * Created by vishal on 7/3/18.
 */

public class MoneyGridAdapter extends BaseAdapter {

    CommanClass cc;

    private Context context;
    private final String[] mobileValues;


    Holder holder;
    public int pos;

    private static LayoutInflater inflater=null;

    static final String[] MOBILE_OS = new String[] {
            "Tranqullidade", "Tedlo","Saudades", "Vergonha", "Orgulho", "Tristeza", "Amor", "Solidao","Esperanca",
            "Decepcao", "Alegria", "Confusao", "Entusiansmo", "Nojo","Ansiedade",
            "Preacupacao", "Raiva", "Desconfianca", "Medo", "Cupla"};


    public MoneyGridAdapter(Context context, String[] mobileValues) {

        this.context = context;
        this.mobileValues = mobileValues;

        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        cc = new CommanClass(context);
    }



    @Override
    public int getCount() {
        return mobileValues.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public class Holder
    {
        TextView tv_money;
        ImageView imageView;
        public LinearLayout lnGrid;
    }

    public View getView(final int position, View convertView, final ViewGroup parent) {


        holder = new Holder();
        View rowView;

        rowView = inflater.inflate(R.layout.todo_emotion_grid_item, null);

        pos = position;

        holder.tv_money = (TextView) rowView.findViewById(R.id.tv_money);
        holder.imageView = (ImageView) rowView.findViewById(R.id.imageView);


        String mobile = mobileValues[position];

        if (mobile.equals("one")) {
            holder.imageView.setImageResource(R.drawable.task_right);
        } else if (mobile.equals("two")) {
            holder.imageView.setImageResource(R.drawable.task_right);
        } else if (mobile.equals("three")) {
            holder.imageView.setImageResource(R.drawable.task_right);
        } else if (mobile.equals("four")) {
            holder.imageView.setImageResource(R.drawable.task_right);
        }else if (mobile.equals("five")) {
            holder.imageView.setImageResource(R.drawable.task_right);
        }else if (mobile.equals("six")) {
            holder.imageView.setImageResource(R.drawable.task_right);
        }
        else if (mobile.equals("seven")) {
            holder.imageView.setImageResource(R.drawable.task_right);
        }
        else if (mobile.equals("eight")) {
            holder.imageView.setImageResource(R.drawable.task_right);
        }else if (mobile.equals("nine")) {
            holder.imageView.setImageResource(R.drawable.task_right);
        }
        else {
            holder.imageView.setImageResource(R.drawable.task_right);
        }


        return rowView;
    }


}
