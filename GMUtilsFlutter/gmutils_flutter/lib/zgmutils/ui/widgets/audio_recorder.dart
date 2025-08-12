import 'dart:async';

import 'package:flutter/material.dart';
import 'package:record/record.dart' as recoder;

import '../../utils/files.dart';
import '../../utils/logs.dart';

class AudioRecorder extends StatefulWidget {
  final void Function(String path) onStop;
  final void Function(String) onError;

  const AudioRecorder({
    super.key,
    required this.onStop,
    required this.onError,
  });

  @override
  State<AudioRecorder> createState() => _AudioRecorderState();
}

class _AudioRecorderState extends State<AudioRecorder> {
  int _recordDuration = 0;
  Timer? _timer;
  late final recoder.AudioRecorder _audioRecorder;
  StreamSubscription<recoder.RecordState>? _recordSub;
  recoder.RecordState _recordState = recoder.RecordState.stop;

  // StreamSubscription<recoder.Amplitude>? _amplitudeSub;
  // recoder.Amplitude? _amplitude;

  @override
  void initState() {
    _audioRecorder = recoder.AudioRecorder();

    _recordSub = _audioRecorder.onStateChanged().listen((recordState) {
      _updateRecordState(recordState);
    });

    /*_amplitudeSub = _audioRecorder
        .onAmplitudeChanged(const Duration(milliseconds: 300))
        .listen((amp) {
      setState(() => _amplitude = amp);
    });*/

    super.initState();
  }

  Future<void> _start() async {
    try {
      if (await _audioRecorder.hasPermission()) {
        const encoder = recoder.AudioEncoder.aacLc;
        //const encoder = recoder.AudioEncoder.wav;

        if (!await _isEncoderSupported(encoder)) {
          return;
        }

        //final devs = await _audioRecorder.listInputDevices();
        const config = recoder.RecordConfig(encoder: encoder, numChannels: 1);

        // Record to file
        var dt = DateTime.now();
        var folder = 'audios_'
            '${dt.year}'
            '${dt.month < 10 ? '0' : ''}${dt.month}'
            '${dt.day < 10 ? '0' : ''}${dt.day}';

        var file = await Files.private(
          '$folder/voice_${dt.millisecondsSinceEpoch}',
          'm4a',
        ).localFile;

        _audioRecorder.start(config, path: file.path);

        _recordDuration = 0;

        _startTimer();
      }
    } catch (e) {
      Logs.print(() => e);
    }
  }

  Future<void> _stop() async {
    final path = await _audioRecorder.stop();

    Logs.print(() => '_AudioRecorderState._stop ----> path: $path');

    if (path != null) {
      widget.onStop(path);

      // downloadWebData(path);
    }
  }

  Future<void> _pause() => _audioRecorder.pause();

  Future<void> _resume() => _audioRecorder.resume();

  void _updateRecordState(recoder.RecordState recordState) {
    setState(() => _recordState = recordState);

    switch (recordState) {
      case recoder.RecordState.pause:
        _timer?.cancel();
        break;
      case recoder.RecordState.record:
        _startTimer();
        break;
      case recoder.RecordState.stop:
        _timer?.cancel();
        _recordDuration = 0;
        break;
    }
  }

  Future<bool> _isEncoderSupported(recoder.AudioEncoder encoder) async {
    final isSupported = await _audioRecorder.isEncoderSupported(
      encoder,
    );

    if (!isSupported) {
      String error = '${encoder.name} is not supported on this platform.';
      error += '\nSupported encoders are:';

      for (final e in recoder.AudioEncoder.values) {
        if (await _audioRecorder.isEncoderSupported(e)) {
          error += '\n- ${encoder.name}';
        }
      }

      Logs.print(() => error);
      widget.onError(error);
    }

    return isSupported;
  }

  @override
  void dispose() {
    _timer?.cancel();
    _recordSub?.cancel();
    // _amplitudeSub?.cancel();
    _audioRecorder.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    var children = <Widget>[];

    if (_recordState == recoder.RecordState.stop) {
      children.add(clipOval(
        const Icon(Icons.mic, color: Colors.white, size: 30),
        Colors.white.withOpacity(0.1),
        onClick: () => _start(),
      ));

      /*children.add(const SizedBox(width: 10));

      children.add(Text(
        App.isEnglish ? "Waiting to record" : "في انتظار التسجيل",
        style: Res.themes.defaultTextStyle(textColor: Colors.white),
      ));*/
    }
    //
    else {
      children.add(clipOval(
        const Icon(Icons.stop, color: Colors.red, size: 30),
        Colors.red.withOpacity(0.1),
        onClick: () => _stop(),
      ));

      children.add(const SizedBox(width: 10));

      //---------------------

      Icon icon;
      Color color;

      if (_recordState == recoder.RecordState.record) {
        icon = const Icon(Icons.pause, color: Colors.red, size: 30);
        color = Colors.red.withOpacity(0.1);
      }
      //
      else {
        final theme = Theme.of(context);
        icon = const Icon(Icons.play_arrow, color: Colors.red, size: 30);
        color = theme.primaryColor.withOpacity(0.1);
      }

      clipOval(
        icon,
        color,
        onClick: () {
          (_recordState == recoder.RecordState.pause) ? _resume() : _pause();
        },
      );

      children.add(const SizedBox(width: 10));

      children.add(_buildTimer());
    }

    return Column(
      mainAxisAlignment: MainAxisAlignment.center,
      children: [
        Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: children,
        ),
        /*if (_amplitude != null) ...[
          const SizedBox(height: 40),
          Text('Current: ${_amplitude?.current ?? 0.0}'),
          Text('Max: ${_amplitude?.max ?? 0.0}'),
        ],*/
      ],
    );
  }

  ClipOval clipOval(
    Icon icon,
    Color color, {
    required VoidCallback onClick,
  }) =>
      ClipOval(
        child: Material(
          color: color,
          child: InkWell(
            onTap: onClick,
            child: SizedBox(width: 56, height: 56, child: icon),
          ),
        ),
      );

  Widget _buildTimer() {
    final String minutes = _formatNumber(_recordDuration ~/ 60);
    final String seconds = _formatNumber(_recordDuration % 60);

    return Text(
      '$minutes : $seconds',
      style: const TextStyle(color: Colors.red),
    );
  }

  String _formatNumber(int number) {
    String numberStr = number.toString();
    if (number < 10) {
      numberStr = '0$numberStr';
    }

    return numberStr;
  }

  void _startTimer() {
    _timer?.cancel();

    _timer = Timer.periodic(const Duration(seconds: 1), (Timer t) {
      setState(() => _recordDuration++);
    });
  }
}

//example
/*
class _MyAppState extends State<MyApp> {
  bool showPlayer = false;
  String? audioPath;

  @override
  void initState() {
    showPlayer = false;
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        body: Center(
          child: showPlayer
              ? Padding(
                  padding: const EdgeInsets.symmetric(horizontal: 25),
                  child: AudioPlayer(
                    source: audioPath!,
                    onDelete: () {
                      setState(() => showPlayer = false);
                    },
                  ),
                )
              : AudioRecorder(
                  onStop: (path) {
                    Logs.print(() => 'Recorded file path: $path');
                    setState(() {
                      audioPath = path;
                      showPlayer = true;
                    });
                  },
                ),
        ),
      ),
    );
  }
 */
