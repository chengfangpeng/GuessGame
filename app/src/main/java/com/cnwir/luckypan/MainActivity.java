package com.cnwir.luckypan;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private LuckyPanView mLuckyPanView;

    private ImageView mStartBtn;

    private int policeNum;

    private static final int POLICE = 2;


    private int killerNum;

    private static final int KILLER = 2;


    private TextView showResult_tv;

    private int indexNum = 0;

    private List<Integer> indexQueue = new ArrayList<Integer>();
    private StringBuilder sb = new StringBuilder();

    private int j;

    private Button showResult;

    public MainActivity() {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLuckyPanView = (LuckyPanView) findViewById(R.id.id_luckypan);
        mStartBtn = (ImageView) findViewById(R.id.id_start_btn);
        showResult_tv = (TextView) findViewById(R.id.showResult);
        showResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



            }
        });
        initIndexQueue();

            mStartBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(j  > 10){

                        Toast.makeText(MainActivity.this, "请重新开始", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (mLuckyPanView.isStart()) {
                        if (!mLuckyPanView.isShouldEnd()) {
                            mLuckyPanView.luckyStop();
                            mStartBtn.setImageResource(R.mipmap.start);
                            showResult_tv.setText(sb);
                            j++;

                        }

                    } else {


//                        int index = (int) (Math.random() * 6);
                        //从排列好的队列里取出值
                        int index = indexQueue.get(j);

                        switch (index) {

                            case 0:

                                if (policeNum < POLICE) {

                                    policeNum++;
                                } else {

                                    index = 5;
                                }
                                break;
                            case 1:


                                break;
                            case 2:
                                if (killerNum < KILLER) {

                                    killerNum++;
                                } else {

                                    index = 5;
                                }

                                break;
                            case 3:

                                break;
                            case 4:


                                break;
                            case 5:
                                break;


                        }

                        mLuckyPanView.luckyStart(index);

                        mStartBtn.setImageResource(R.mipmap.stop);
                        indexNum++;

                        switch (index) {

                            case 0:

                                sb.append(indexNum + ".警察 ");

                                break;
                            case 1:
                                sb.append(indexNum + ".平民 ");
                                break;
                            case 2:
                                sb.append(indexNum + ".杀手 ");
                                break;
                            case 3:
                                sb.append(indexNum + ".平民 ");
                                break;
                            case 4:
                                sb.append(indexNum + ".妹子 ");
                                break;
                            case 5:
                                sb.append(indexNum + ".平民 ");
                                break;

                        }


                    }


                }
            });

    }

    /**
     * 初始化选择队列
     */

    private void initIndexQueue() {

        indexQueue.add(3);
        indexQueue.add(1);
        indexQueue.add(3);
        indexQueue.add(5);
        indexQueue.add(1);
        indexQueue.add(3);
        indexQueue.add(5);
        int i = 0;
        int random = (int) (Math.random() * (7 + i));
        indexQueue.add(random, 0);
        i++;
        random = (int) (Math.random() * (7 + i));

        indexQueue.add(random, 0);
        i++;
        random = (int) (Math.random() * (7 + i));

        indexQueue.add(random, 2);
        i++;
        random = (int) (Math.random() * (7 + i));

        indexQueue.add(random, 2);

        System.out.println(indexQueue.toString());
    }
}
