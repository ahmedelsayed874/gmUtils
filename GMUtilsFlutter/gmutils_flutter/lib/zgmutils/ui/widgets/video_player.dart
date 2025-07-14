import 'dart:async';
import 'dart:io';
import 'package:flutter/material.dart';
import 'package:video_player/video_player.dart' as vp;

import '../../gm_main.dart';
import '../../resources/app_theme.dart';
import '../../utils/logs.dart';
import 'web_view_screen.dart';

///https://docs.flutter.dev/cookbook/plugins/play-video

class VideoPlayer extends StatefulWidget {
  final String? videoTitle;
  final String? videoUrl;
  final File? videoFile;
  final bool autoPlay;
  final bool looping;
  final bool allowEnlarge;
  final VoidCallback? onVideoLoadError;
  final VoidCallback? onVideoPlayed;
  final VoidCallback? onVideoCompleted;

  const VideoPlayer({
    this.videoTitle,
    this.videoUrl,
    this.videoFile,
    this.autoPlay = false,
    this.looping = true,
    this.allowEnlarge = false,
    this.onVideoLoadError,
    this.onVideoPlayed,
    this.onVideoCompleted,
    super.key,
  });

  @override
  State<VideoPlayer> createState() => _VideoPlayerState();
}

class _VideoPlayerState extends State<VideoPlayer> {
  late vp.VideoPlayerController _controller;
  late Future<void> _initializeVideoPlayerFuture;
  Timer? timer;
  VoidCallback? onVideoCompleted;

  @override
  void initState() {
    super.initState();

    if (widget.videoUrl != null) {
      _controller = vp.VideoPlayerController.networkUrl(
        Uri.parse(widget.videoUrl!),
      );
    }
    //
    else if (widget.videoFile != null) {
      _controller = vp.VideoPlayerController.file(widget.videoFile!);
    }

    _initializeVideoPlayerFuture = _controller.initialize();

    _controller.setLooping(widget.looping);

    onVideoCompleted = widget.onVideoCompleted;
  }

  @override
  void dispose() {
    // Ensure disposing of the VideoPlayerController to free up resources.
    _controller.dispose();

    timer?.cancel();

    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return FutureBuilder(
      future: _initializeVideoPlayerFuture,
      builder: (context, snapshot) {
        if (snapshot.connectionState == ConnectionState.done) {
          //run observer
          if (!_controller.value.hasError && onVideoCompleted != null) {
            _observeVideoComplete();
          }

          //run autoplay
          if (!_controller.value.hasError && widget.autoPlay) {
            if (!_controller.value.isPlaying) {
              Future.delayed(Duration(milliseconds: 300), _playOrPause);
            }
          }

          //on error
          if (_controller.value.hasError && widget.onVideoLoadError != null) {
            timer?.cancel();
            Future.delayed(Duration(milliseconds: 300), () {
              widget.onVideoLoadError?.call();
            });
          }

          return AspectRatio(
            aspectRatio: _controller.value.aspectRatio,
            child: Stack(
              children: [
                if (_controller.value.hasError)
                  Container(
                    color: App.isLightTheme == true
                        ? Colors.black
                        : Colors.grey[800],
                  ),

                //video player
                vp.VideoPlayer(_controller),

                if (_controller.value.isPlaying)
                  GestureDetector(
                    onTap: () => setState(() {
                      _controller.pause();
                    }),
                  ),

                //play/pause
                Align(
                  alignment: Alignment.center,
                  child: playPauseButton(),
                ),

                //enlarge video
                if (widget.videoUrl != null && widget.allowEnlarge)
                  Align(
                    alignment:
                        App.isEnglish ? Alignment.topRight : Alignment.topLeft,
                    child: GestureDetector(
                      onTap: () => enlargeVideo(),
                      child: const Padding(
                        padding: EdgeInsets.all(8.0),
                        child: Icon(
                          Icons.featured_video_rounded,
                          color: Colors.white,
                        ),
                      ),
                    ),
                  ),
              ],
            ),
          );
        }
        //
        else {
          return const Center(
            child: SizedBox(
              width: 20,
              height: 20,
              child: CircularProgressIndicator(),
            ),
          );
        }
      },
    );
  }

  int playTimes = 0;

  Widget playPauseButton() {
    return IconButton(
      onPressed: _playOrPause,
      icon: Icon(
        _controller.value.isPlaying
            ? /*(_userNeedToPause ? Icons.pause : null)*/ null
            : Icons.play_circle_outline,
        color: Colors.white,
        size: 40,
      ),
    );
  }

  void _playOrPause() {
    if (_controller.value.hasError) {
      playTimes++;
      if (playTimes == 1) {
        widget.onVideoPlayed?.call();
      }
      enlargeVideo();
    }
    //
    else {
      setState(() {
        if (_controller.value.isPlaying) {
          _controller.pause();
        }
        //
        else {
          playTimes++;
          if (playTimes == 1) {
            widget.onVideoPlayed?.call();
          }
          _controller.play();
        }
      });
    }
  }

  void enlargeVideo() {
    try {
      _controller.pause();
    } catch (e) {}

    WebViewScreen.show(
      toolbarTitle: widget.videoTitle ?? '',
      url: widget.videoUrl!,
    );
  }

  void _observeVideoComplete() {
    timer = Timer.periodic(
      Duration(seconds: 1),
      (timer) {
        if (!_controller.value.hasError) {
          if (_controller.value.duration.inSeconds ==
              _controller.value.position.inSeconds) {
            Logs.print(() =>
                '${this.runtimeType}._observeVideoComplete ----> video completed');
            timer.cancel();
            onVideoCompleted?.call();
            onVideoCompleted = null;
          }
        } else {
          timer.cancel();
        }
      },
    );
  }
}
