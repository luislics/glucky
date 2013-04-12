package feedrss;
 
import java.util.List;

import com.example.glucky.R;
 
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
 

 
/**
 * Main application activity.
 * 
 * Update: Downloading RSS data in an async task 
 * 
 * @author ITCuties
 *
 */
public class AndroidRssReader extends Activity {
     
    // A reference to the local object
    private AndroidRssReader local;
     ListView itcItems;
    /** 
     * This method creates main application view
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set view
        setContentView(R.layout.list);
        itcItems = (ListView) findViewById(R.id.listrss);
        // Set reference to this activity
        local = this;
         
        GetRSSDataTask task = new GetRSSDataTask();
         
        // Start download RSS task
        task.execute("http://datos-pedagogicos-para-ninos.webnode.cl/rss/all.xml");
         
        // Debug the thread name
        Log.d("ITCRssReader", Thread.currentThread().getName());
    }
     
    private class GetRSSDataTask extends AsyncTask<String, Void, List<RSSItem> > {
        @Override
        protected List<RSSItem> doInBackground(String... urls) {
             
            // Debug the task thread name
            Log.d("ITCRssReader", Thread.currentThread().getName());
             
            try {
                // Create RSS reader
                RssReader rssReader = new RssReader(urls[0]);
             
                // Parse RSS, get items
                return rssReader.getItems();
             
            } catch (Exception e) {
                Log.e("ITCRssReader", e.getMessage());
            }
             
            return null;
        }
         
        @Override
        protected void onPostExecute(List<RSSItem> result) {
             
            // Get a ListView from main view
            
                         
            // Create a list adapter
            ArrayAdapter<RSSItem> adapter = new ArrayAdapter<RSSItem>(local,android.R.layout.simple_list_item_1, result);
            // Set list adapter for the ListView
            itcItems.setAdapter(adapter);
                         
            // Set list view item click listener
            itcItems.setOnItemClickListener(new ListListener(result, local));
        }
    }   
}