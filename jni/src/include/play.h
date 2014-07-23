#ifndef PLAYER_H
#define PLAYER_H
int player_init();
int player_prepare(const char *url, int minFrames, int maxAnalyzeDuration);
int player_main();
int player_exit();
int getDuration();
int getCurrentPosition();
int seekTo(int msec);
int streamPause();
int isPlay();
char* getyingguiData();
int changeAudio(int index);
int set_aspect_ratio(int aspect_ratio_type);
int set_screen_params(int width, int height);

#endif

