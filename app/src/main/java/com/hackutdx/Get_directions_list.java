package com.hackutdx;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;

import com.hackutdx.MainActivity;
import com.hackutdx.Map_Stuff;

import java.util.ArrayList;
import java.util.List;

public class Get_directions_list extends AsyncTask<Context, Void, Void>
{
    public boolean finished;
    public static List<Map_Stuff.Step_Tuple> generated_steps_list;
    public static List<Map_Stuff.Step_Tuple> get_steps(Context context)
    {
        generated_steps_list = new ArrayList<>();
        Get_directions_list gdl = new Get_directions_list();
        gdl.execute(context);
        return generated_steps_list;
    }

    public List<Map_Stuff.Step_Tuple> steps;
    @Override
    protected Void doInBackground(Context... contexts) {
        try {
            finished = false;
            Looper.prepare();
            Map_Stuff m = new Map_Stuff(contexts[0]);
            steps = m.get_steps(m.read_url(m.getURL("2801 Rutford Avenue")));
            int i = 0;
            for(Map_Stuff.Step_Tuple step : steps)
            {
                Log.d("final_step_" + i++, step.str + " " + step.distance_in_meters);
                generated_steps_list.add(new Map_Stuff.Step_Tuple(step.str, step.distance_in_meters));
            }
            finished = true;

        }catch(Exception e){
            Log.d("StackTrace",e.getMessage());
            StackTraceElement[] ste_arr = e.getStackTrace();
            for(int i = 0; i < ste_arr.length; i++)
            {
                Log.d("StackTrace_" + i, ste_arr[i].toString());
            }

        }
        return null;
    }
}