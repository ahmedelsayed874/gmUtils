import 'dart:async';
import 'dart:io';

import 'package:gmutils_flutter/resources/_resources.dart';
import 'package:gmutils_flutter/zgmutils/gm_main.dart';
import 'package:gmutils_flutter/zgmutils/ui/widgets/web_view_screen.dart';
import 'package:gmutils_flutter/zgmutils/utils/logs.dart';
import 'package:flutter/material.dart';
import 'package:video_player/video_player.dart' as vp;

///https://docs.flutter.dev/cookbook/plugins/play-video

class VideoPlayer extends StatefulWidget {
  final String? videoUrl;
  final File? videoFile;
  final bool looping;
  final bool allowEnlarge;
  final VoidCallback? onVideoPlayed;

  const VideoPlayer({
    this.videoUrl,
    this.videoFile,
    this.looping = true,
    this.allowEnlarge = false,
    this.onVideoPlayed,
    super.key,
  });

  @override
  State<VideoPlayer> createState() => _VideoPlayerState();

  static double? getAspectRatio(String? videoUrl, File? videoFile) {
    return _VideoPlayerState.getAspectRatio(videoUrl, videoFile);
  }
}

class _VideoPlayerState extends State<VideoPlayer> {
  static final Map<String, double?> _aspectRatio = {};

  static double? getAspectRatio(String? videoUrl, File? videoFile) {
    String key = '';
    if (videoUrl != null) key = videoUrl;
    if (videoFile != null) key = videoFile.path;
    return _aspectRatio[key];
  }

  static void setAspectRatio(
    String? videoUrl,
    File? videoFile, {
    required double aspectRatio,
  }) {
    String key = '';
    if (videoUrl != null) key = videoUrl;
    if (videoFile != null) key = videoFile.path;
    _aspectRatio[key] = aspectRatio;
  }

  //------------------------

  late vp.VideoPlayerController _controller;
  bool hasError = false;

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

    _controller.initialize().then((v) {
      setAspectRatio(
        widget.videoUrl,
        widget.videoFile,
        aspectRatio: _controller.value.aspectRatio,
      );
      setState(() {});
    }).onError((e, s) {
      setAspectRatio(
        widget.videoUrl,
        widget.videoFile,
        aspectRatio: _controller.value.aspectRatio,
      );
      Logs.print(() => '_VideoPlayerState.initialize('
          'videoUrl: ${widget.videoUrl}, '
          'videoFile: ${widget.videoFile}, '
          ') --> error: $e,,,,,stack: $s');
      setState(() {
        hasError = true;
      });
    });

    _controller.setLooping(widget.looping);
  }

  @override
  void dispose() {
    // Ensure disposing of the VideoPlayerController to free up resources.
    _controller.dispose();

    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    if (_controller.value.isInitialized || hasError) {
      return AspectRatio(
        aspectRatio: _controller.value.aspectRatio,
        child: Stack(
          children: [
            if (hasError || _controller.value.hasError)
              Container(color: Colors.black),

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
            if (widget.videoUrl != null)
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
    } else {
      return const Center(
        child: CircularProgressIndicator(),
      );
    }
  }

  int playTimes = 0;

  Widget playPauseButton() {
    return IconButton(
      onPressed: () {
        if (_controller.value.hasError) {
          playTimes++;
          if (playTimes == 1) {
            widget.onVideoPlayed?.call();
          }
          enlargeVideo();
        } else {
          setState(() {
            if (_controller.value.isPlaying) {
              _controller.pause();
            } else {
              playTimes++;
              if (playTimes == 1) {
                widget.onVideoPlayed?.call();
              }
              _controller.play();
            }
          });
        }
      },
      icon: Icon(
        _controller.value.isPlaying
            ? /*(_userNeedToPause ? Icons.pause : null)*/ null
            : Icons.play_circle_outline,
        color: Colors.white,
        size: 40,
      ),
    );
  }

  Widget playPauseButton0() {
    return IconButton(
      onPressed: () {
        setState(() {
          if (_controller.value.isPlaying) {
            _controller.pause();
          } else {
            _controller.play();
          }
        });
      },
      icon: Icon(
        _controller.value.isPlaying ? Icons.pause : Icons.play_arrow,
        color: Res.themes.colors.primary,
      ),
    );
  }

  void enlargeVideo() {
    try {
      _controller.pause();
    } catch (e) {}

    WebViewScreen.show(
      toolbarTitle: Res.strings.appName,
      url: widget.videoUrl!,
    );
  }
}
