package edu.temple.lab7;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class BookListFragment extends Fragment {

    ArrayAdapter<arrList> bookAdapter;
    ListView viewonList;
    int listNum =android.R.layout.simple_list_item_activated_1;
    OnItemSelectedListener listener;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        listener = (OnItemSelectedListener) activity;

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.booklist_frag, container,
                false);

        viewonList = (ListView) view.findViewById(R.id.booklist);
        viewonList.setAdapter(bookAdapter);
        viewonList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View book, int position,
                                    long bookId) {
                arrList lis = bookAdapter.getItem(position);

                listener.onItemSelected(lis);
            }
        });
        return view;
    }

    public void setActivateOnItemClick(boolean onClick) {

        if (onClick){
            viewonList.setChoiceMode(1);
        }else{
            viewonList.setChoiceMode(0);

        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArrayList<arrList> books = arrList.displayBooks();

        bookAdapter = new ArrayAdapter<arrList>(getActivity(),
                listNum, books);
    }


    public interface OnItemSelectedListener {
        public void onItemSelected(arrList val);
    }
}
