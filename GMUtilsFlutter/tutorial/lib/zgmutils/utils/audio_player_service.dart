import 'dart:async';
import 'package:audioplayers/audioplayers.dart' as ap;

class AudioPlayerService {
  final _audioPlayer = ap.AudioPlayer()..setReleaseMode(ap.ReleaseMode.stop);
  ap.Source? _source;
  StreamSubscription<void>? _playerStateChangedSubscription;
  StreamSubscription<Duration?>? _durationChangedSubscription;
  StreamSubscription<Duration>? _positionChangedSubscription;
  Duration? _position;
  Duration? _duration;

  AudioPlayerService() {
    _playerStateChangedSubscription = _audioPlayer.onPlayerComplete.listen(
      (state) async {
        await stop();
      },
    );

    _positionChangedSubscription = _audioPlayer.onPositionChanged.listen(
      (position) {
        _position = position;
      },
    );

    _durationChangedSubscription = _audioPlayer.onDurationChanged.listen(
      (duration) {
        _duration = duration;
      },
    );
  }

  Future<void> setAudioUrl(String url) async {
    _source = ap.UrlSource(url);
    await _audioPlayer.setSource(_source!);
  }

  Future<void> setAudioFile(String filePath) async {
    _source = ap.DeviceFileSource(filePath);
    await _audioPlayer.setSource(_source!);
  }

  Future<void> setAudioAsset(String assetName) async {
    _source = ap.AssetSource(assetName);
    await _audioPlayer.setSource(_source!);
  }

  String? get audioPath {
    if (_source is ap.UrlSource) {
      return (_source as ap.UrlSource).url;
    } else if (_source is ap.DeviceFileSource) {
      return (_source as ap.DeviceFileSource).path;
    } else if (_source is ap.AssetSource) {
      return (_source as ap.AssetSource).path;
    } else {
      return null;
    }
  }

  bool get isPlaying => _audioPlayer.state == ap.PlayerState.playing;

  Future<void> play() {
    assert(_source != null);
    return _audioPlayer.play(_source!);
  }

  Future<void> pause() async {
    await _audioPlayer.pause();
  }

  Future<void> stop() async {
    await _audioPlayer.stop();
  }

  void seekTo(int millisecond) {
    _audioPlayer.seek(Duration(milliseconds: millisecond));
  }

  Duration? get position => _position;

  Duration? get duration => _duration;

  void dispose() {
    _playerStateChangedSubscription?.cancel();
    _positionChangedSubscription?.cancel();
    _durationChangedSubscription?.cancel();
    _audioPlayer.dispose();
  }
}
