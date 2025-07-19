import 'dart:async';

import 'package:audioplayers/audioplayers.dart' as ap;
import 'package:audioplayers/audioplayers.dart';
import 'package:gmutils_flutter/resources/_resources.dart';
import 'package:gmutils_flutter/zgmutils/gm_main.dart';
import 'package:flutter/material.dart';

class AudioPlayer extends StatefulWidget {
  /// Path from where to play recorded audio
  final String? audioUrl;
  final String? filePath;
  final bool showDuration;
  final Color? controlColor;
  final Color? sliderColor;

  const AudioPlayer({
    super.key,
    required this.audioUrl,
    required this.filePath,
    this.controlColor,
    this.sliderColor,
    this.showDuration = true,
  }) : assert(audioUrl != null || filePath != null);

  @override
  AudioPlayerState createState() => AudioPlayerState();
}

class AudioPlayerState extends State<AudioPlayer> {
  static const double _controlSize = 46;

  final _audioPlayer = ap.AudioPlayer()..setReleaseMode(ReleaseMode.stop);
  late StreamSubscription<void> _playerStateChangedSubscription;
  late StreamSubscription<Duration?> _durationChangedSubscription;
  late StreamSubscription<Duration> _positionChangedSubscription;
  Duration? _position;
  Duration? _duration;

  Source get _source => widget.audioUrl != null
      ? ap.UrlSource(widget.audioUrl!)
      : ap.DeviceFileSource(widget.filePath!);

  @override
  void initState() {
    _playerStateChangedSubscription =
        _audioPlayer.onPlayerComplete.listen((state) async {
      await stop();
    });
    _positionChangedSubscription = _audioPlayer.onPositionChanged.listen(
      (position) => setState(() {
        _position = position;
      }),
    );
    _durationChangedSubscription = _audioPlayer.onDurationChanged.listen(
      (duration) => setState(() {
        _duration = duration;
      }),
    );

    _audioPlayer.setSource(_source);

    super.initState();
  }

  @override
  void dispose() {
    _playerStateChangedSubscription.cancel();
    _positionChangedSubscription.cancel();
    _durationChangedSubscription.cancel();
    _audioPlayer.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return LayoutBuilder(
      builder: (context, constraints) {
        return Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Row(
              mainAxisSize: MainAxisSize.max,
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: <Widget>[
                _buildControl(),
                _buildSlider(constraints.maxWidth),
              ],
            ),
            if (widget.showDuration) Text('${_duration ?? 0.0}'),
          ],
        );
      },
    );
  }

  Widget _buildControl() {
    Widget icon;
    Color controlColor = widget.controlColor ?? Res.themes.colors.secondary;
    Color controlColorBg = controlColor.withOpacity(0.3);

    if (_audioPlayer.state == ap.PlayerState.playing) {
      icon = Icon(Icons.pause, color: controlColor, size: 30);
    } else {
      icon = Transform.rotate(
          angle: App.isEnglish ? 0 : 3.14,
          child: Icon(Icons.play_arrow, color: controlColor, size: 30,));
    }

    return ClipOval(
      child: Material(
        color: controlColorBg,
        child: InkWell(
          child: SizedBox(
            width: _controlSize,
            height: _controlSize,
            child: icon,
          ),
          onTap: () {
            if (_audioPlayer.state == ap.PlayerState.playing) {
              pause();
            } else {
              play();
            }
          },
        ),
      ),
    );
  }

  Widget _buildSlider(double widgetWidth) {
    bool canSetValue = false;
    final duration = _duration;
    final position = _position;

    if (duration != null && position != null) {
      canSetValue = position.inMilliseconds > 0;
      canSetValue &= position.inMilliseconds < duration.inMilliseconds;
    }

    double width = widgetWidth - _controlSize;

    var color = widget.sliderColor ?? Res.themes.colors.secondary;

    return SizedBox(
      width: width,
      child: Slider(
        activeColor: color,
        inactiveColor: color.withOpacity(0.6),
        onChanged: (v) {
          if (duration != null) {
            final position = v * duration.inMilliseconds;
            _audioPlayer.seek(Duration(milliseconds: position.round()));
          }
        },
        value: canSetValue && duration != null && position != null
            ? position.inMilliseconds / duration.inMilliseconds
            : 0.0,
      ),
    );
  }

  Future<void> play() => _audioPlayer.play(_source);

  Future<void> pause() async {
    await _audioPlayer.pause();
    setState(() {});
  }

  Future<void> stop() async {
    await _audioPlayer.stop();
    setState(() {});
  }
}
