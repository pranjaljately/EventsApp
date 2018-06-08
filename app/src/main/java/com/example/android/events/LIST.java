package com.example.android.events;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class LIST extends AppCompatActivity implements View.OnClickListener
{

    ListView listview;



    List<String> input;

    ArrayList<String> store;

    Button addbutton;

    MyAdapter myAdapter;

    EditText edit;

    TextView display;

    ImageButton images;

    ImageButton images2;

    TextView text;

    EditText text2;

    int selectedListPosition;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        listview = findViewById(R.id.listview);


        input = new ArrayList<String>() ;
        input.add("Hello");


        store = new ArrayList<>();

        addbutton = findViewById(R.id.addbutton);

//        display = findViewById(R.id.display);

        edit = findViewById(R.id.edittext);

        myAdapter = new MyAdapter(this, R.layout.items_list,input);






        addbutton.setOnClickListener(LIST.this);

        listview.setAdapter(myAdapter);

        myAdapter.notifyDataSetChanged();






        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                String myItemSelected;



                myItemSelected = parent.getAdapter().getItem(position).toString();



                display.setText(myItemSelected);

                Toast.makeText(LIST.this ,"Content Edited" ,Toast.LENGTH_SHORT).show();
            }
        });





    }








    @Override
    public void onClick(View v)
    {
        String whatUserEntered = edit.getText().toString();




        if(whatUserEntered.trim().equals("") && v.getId() == addbutton.getId())
        {
            Toast.makeText(this ,"No Content Entered" ,Toast.LENGTH_SHORT).show();

            return;
        }
        else if(v.getId() == addbutton.getId())
        {
            input.add(0,whatUserEntered);

            edit.setText("");




            myAdapter.notifyDataSetChanged();


        }






    }




    public class MyAdapter extends ArrayAdapter
    {

        private Context context;



        private List<String> input;

        boolean[] arrBgcolor;


//        private TextView store;
//
//        private ImageButton search;
//
//        private ImageButton remove;

        private int resource;
        private LayoutInflater inflater;


        public MyAdapter(@NonNull Context context, int resource, @NonNull List<String> objects) {
            super(context, resource, objects);

            input = objects;
            this.resource = resource;
            inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);



        }









//

        @SuppressLint("CutPasteId")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            if(convertView == null)
            {
                convertView = inflater.inflate(resource, null);
            }

            images = convertView.findViewById(R.id.search);

            images2 = convertView.findViewById(R.id.remove);

            text = convertView.findViewById(R.id.store);

            text2 = convertView.findViewById(R.id.input);

            text.setText("Store");

            text2.setText(input.get(position));




//            final View finalConvertView = convertView;

            final View finalConvertView1 = convertView;
            convertView.setOnClickListener(new View.OnClickListener() {


                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
//                    String myItemSels = getItem(position).toString();

//                    finalConvertView.requestFocus();

//                    display.setText(text2.getText().toString());




//                    finalConvertView1.setBackgroundColor(Color.parseColor("#87CEFA"));

//                    notifyDataSetChanged();

//                    Toast.makeText(MainActivity.this ,"des" ,Toast.LENGTH_SHORT).show();
                }
            });





            images2.setOnClickListener(new View.OnClickListener()
            {


                @Override
                public void onClick(View v)
                {
                    selectedListPosition = position;
                    input.remove(position);



                    myAdapter.notifyDataSetChanged();


                    Toast.makeText(LIST.this ,"deleted" ,Toast.LENGTH_SHORT).show();



                }
            });

            return convertView;





        }
    }





}
