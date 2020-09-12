package com.rakpak.pak_parak.BottomSheed;
import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.rakpak.pak_parak.R;

public class AuidoButtomSheed extends BottomSheetDialogFragment {

    String url;
    private Boolean isplaying;
    private Handler handler = new Handler();
    private SeekBar playsikbar;
    private TextView textcurrenttime, texttotoltime;
    private ImageView playpush;
    private MediaPlayer mediaPlayer;
    private ProgressDialog Mprogress;

    public AuidoButtomSheed(String url) {
        this.url = url;
    }


    private RelativeLayout cross;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.music_player_bottom_sheed, container, false);


        cross = view.findViewById(R.id.Cross);
        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });



        textcurrenttime = view.findViewById(R.id.StartTimerID);
        texttotoltime = view.findViewById(R.id.EndTimerID);
        playsikbar = view.findViewById(R.id.SeekbarID);
        playpush = view.findViewById(R.id.PlaypausIconID);

        mediaPlayer = new MediaPlayer();


        mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {

                if(what == mp.MEDIA_INFO_BUFFERING_START){
                    playpush.setEnabled(false);
                }
                else if(what == mp.MEDIA_INFO_BUFFERING_END){
                    playpush.setEnabled(true);
                }

                return false;
            }
        });




        prepairmedaplayer(url);



        playsikbar.setMax(100);

        playpush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    handler.removeCallbacks(updater);
                    mediaPlayer.pause();
                    playpush.setImageResource(R.drawable.play_icon);
                }
                else {
                    mediaPlayer.start();
                    playpush.setImageResource(R.drawable.push_icon);
                    updatesikbar();
                }



            }
        });


        playsikbar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                SeekBar seekBar = (SeekBar) v;
                int playposition = (mediaPlayer.getDuration() / 100) * seekBar.getProgress();
                mediaPlayer.seekTo(playposition);
                textcurrenttime.setText(milisecondtimer(mediaPlayer.getCurrentPosition()));

                return false;
            }
        });

        return view;
    }



    private void prepairmedaplayer(String myuri){

        if(!url.isEmpty()){
            try {
                mediaPlayer.setDataSource(myuri);
                mediaPlayer.prepare();
                texttotoltime.setText(milisecondtimer(mediaPlayer.getDuration()));
            }
            catch (Exception e){
                Toast.makeText(getContext(), "error", Toast.LENGTH_LONG).show();
            }
        }


    }

    private Runnable updater = new Runnable() {
        @Override
        public void run() {
            updatesikbar();
            long currentDuraction = mediaPlayer.getDuration();
            textcurrenttime.setText(milisecondtimer(currentDuraction));
        }
    };

    private void updatesikbar(){
        if(mediaPlayer.isPlaying()){
            playsikbar.setProgress((int) (((float)mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration()) * 100));

            handler.postDelayed(updater, 1000);
        }
    }

    private String milisecondtimer(long miliscond){
        String timerString = "";
        String secondString;

        int hours = (int)(miliscond / (1000 * 60 * 60));
        int minitus =  (int)(miliscond % (1000 * 60 * 60)) / (100 * 60);
        int second = (int) ((miliscond % (1000 * 60 * 60)) % (1000 * 60) / 1000);

        if(hours > 0){
            timerString = hours + ":";
        }
        if(second < 10){
            secondString = ""+second;
        }
        else {
            secondString = ""+second;
        }

        timerString = timerString + minitus + ":"+secondString;

        return  timerString;
    }


    @Override
    public void onStop() {
        mediaPlayer.stop();
        super.onStop();
    }
}


