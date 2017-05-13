import android.content.Intent;
import android.view.MenuItem;

/**
 * Created by anubhabmajumdar on 1/4/17.
 */

public class menu_handler
{
    public void openHydrationSetting(MenuItem item)
    {
        Intent intent = new Intent(this, HydrationSettingActivity.class);
        startActivity(intent);
    }
}
