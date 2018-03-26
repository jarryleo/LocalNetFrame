package cn.leo.nio_client.core;

import android.app.ActivityManager;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.util.List;

/**
 * Created by Leo on 2017/9/18.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)//API需要在21及以上
public class ClientJob extends JobService {
    private int kJobId = 0;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        scheduleJob(getJobInfo());
        Log.i("job---", "onStartJob: 启动定时任务");
        return START_NOT_STICKY;
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        String serviceName = getPackageName() + ".core.ClientService";//改成你需要保活的服务名：全包名
        Log.i("job---", "onStartJob: 定时任务执行" + serviceName);
        boolean isLocalServiceWork = isServiceWork(this, serviceName);
        if (!isLocalServiceWork) {
            this.startService(new Intent(this, ClientService.class));
        }
        jobFinished(params, true);//任务执行完后记得调用jobFinished通知系统释放相关资源
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        scheduleJob(getJobInfo());
        return true;
    }

    public void scheduleJob(JobInfo t) {
        JobScheduler tm =
                (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        if (tm != null) {
            tm.schedule(t);
        }
    }

    public JobInfo getJobInfo() {
        JobInfo.Builder builder = new JobInfo.Builder(kJobId,
                new ComponentName(getPackageName(), ClientJob.class.getName()));
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        //builder.setPersisted(true);
        builder.setRequiresCharging(false);
        builder.setRequiresDeviceIdle(false);
        builder.setMinimumLatency(1000);
        builder.setOverrideDeadline(3000);
        builder.setBackoffCriteria(5000, JobInfo.BACKOFF_POLICY_LINEAR);
        //builder.setPeriodic(1000);
        return builder.build();
    }

    //判断Service是否在存活
    public boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(100);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName();
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }
}

