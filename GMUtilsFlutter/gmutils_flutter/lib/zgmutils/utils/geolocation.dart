import 'dart:async';
import 'dart:math';

import 'package:geolocator/geolocator.dart';

import 'calculations.dart';
import 'logs.dart';

class LocationData {
  double latitude;
  double longitude;

  LocationData({
    required this.latitude,
    required this.longitude,
  });
}

//==============================================================================

abstract class Geolocation {
  static Geolocation? _instance;

  static Geolocation instance({bool dummy = false}) =>
      _instance ??= (dummy ? GeolocationMock() : GeolocationReal());

  Future<bool> isGpsServiceEnabled();

  Future<bool> hasPermissions();

  Future<bool> requestPermission();

  Future<bool> openLocationSetting();

  Future<LocationData?> getCurrentPosition();

  void addOnPositionUpdated({
    required String tag,
    required void Function(LocationData) listener,
    Function()? onError,
  });

  void removeOnPositionUpdated({required String tag});

  double calculateDistance({
    required double lat1,
    required double lng1,
    required double lat2,
    required double lng2,
    required bool inKilometers,
  });
}

//==============================================================================

class GeolocationReal extends Geolocation {
  @override
  Future<bool> isGpsServiceEnabled() async {
    bool serviceEnabled = await Geolocator.isLocationServiceEnabled();
    return serviceEnabled;
  }

  @override
  Future<bool> hasPermissions() async {
    LocationPermission permission = await Geolocator.checkPermission();
    return !(permission == LocationPermission.denied ||
        permission == LocationPermission.deniedForever ||
        permission == LocationPermission.unableToDetermine);
  }

  @override
  Future<bool> requestPermission() async {
    LocationPermission permission = await Geolocator.requestPermission();
    return !(permission == LocationPermission.denied ||
        permission == LocationPermission.deniedForever ||
        permission == LocationPermission.unableToDetermine);
  }

  @override
  Future<bool> openLocationSetting() {
    return Geolocator.openLocationSettings();
  }

  @override
  Future<LocationData?> getCurrentPosition() async {
    try {
      Logs.print(() => 'Geolocation.getCurrentPosition()');
      var p = await Geolocator.getCurrentPosition();
      Logs.print(() => 'Geolocation.getCurrentPosition ---> LocationData: $p');
      return LocationData(
        latitude: p.latitude,
        longitude: p.longitude,
      );
    } catch (e) {
      Logs.print(() => 'Geolocation.getCurrentPosition --> EXCEPTION:: $e');
      return null;
    }
  }

  StreamSubscription<Position>? _positionStreamListener;

  StreamSubscription<Position>? get positionStreamListener =>
      _positionStreamListener;

  final Map<String, void Function(LocationData p1)> _listners = {};

  @override
  void addOnPositionUpdated({
    required String tag,
    required void Function(LocationData p1) listener,
    Function()? onError,
  }) {
    //Log s.print(() => 'Geolocation.setOnPositionUpdated()');

    _listners[tag] = listener;

    _positionStreamListener = Geolocator.getPositionStream(
      locationSettings: const LocationSettings(
          accuracy: LocationAccuracy.best, distanceFilter: 0),
    ).listen(
      (event) {
        //Log s.print(() => 'Geolocation.setOnPositionUpdated.onUpdate --> $event',);
        _listners.forEach((k, v) => v(LocationData(
              latitude: event.latitude,
              longitude: event.longitude,
            )));
      },
      onError: (o, s) {
        //Log s.print(() => 'Geolocation.setOnPositionUpdated.ERROR --> $s');
        onError?.call();
      },
      cancelOnError: false,
    );
  }

  @override
  void removeOnPositionUpdated({required String tag}) {
    _listners.remove(tag);

    if (_listners.isEmpty) {
      _positionStreamListener?.cancel();
      _positionStreamListener = null;
    }
  }

  /// distance in KM
  @override
  double calculateDistance({
    required double lat1,
    required double lng1,
    required double lat2,
    required double lng2,
    required bool inKilometers,
  }) {
    var distance = Calculations().calculateDistanceBetweenPoints(
      lat1: lat1,
      lng1: lng1,
      lat2: lat2,
      lng2: lng2,
      inKilometers: inKilometers,
    );
    return distance;
  }
}

class GeolocationMock extends Geolocation {
  @override
  Future<bool> hasPermissions() async {
    return true;
  }

  @override
  Future<bool> isGpsServiceEnabled() async {
    return true;
  }

  @override
  Future<bool> openLocationSetting() async {
    return true;
  }

  @override
  Future<bool> requestPermission() async {
    return true;
  }

  @override
  double calculateDistance({
    required double lat1,
    required double lng1,
    required double lat2,
    required double lng2,
    required bool inKilometers,
  }) {
    var distance = Calculations().calculateDistanceBetweenPoints(
      lat1: lat1,
      lng1: lng1,
      lat2: lat2,
      lng2: lng2,
      inKilometers: inKilometers,
    );
    return distance;
  }

  //============================================================================

  LocationData? locationData;

  @override
  Future<LocationData?> getCurrentPosition() async {
    return locationData;
  }

  bool _enablePositionUpdate = false;
  final List<LocationData> _someLocations = [
    LocationData(latitude: 30.0001, longitude: 30.0001),
    //LocationData(latitude: 30.0002, longitude: 30.0002),
    LocationData(latitude: 30.0003, longitude: 30.0003),
  ];

  final Map<String, void Function(LocationData p1)> _listners = {};

  @override
  void addOnPositionUpdated({
    required String tag,
    required void Function(LocationData p1) listener,
    Function()? onError,
  }) async {
    _listners[tag] = listener;

    if (_enablePositionUpdate) return;
    _enablePositionUpdate = true;

    while (_enablePositionUpdate) {
      await Future.delayed(const Duration(seconds: 10), () {
        locationData = _someLocations[Random().nextInt(_someLocations.length)];
        _listners.forEach((k, v) => v(locationData!));
      });
    }
  }

  @override
  void removeOnPositionUpdated({required String tag}) {
    _listners.remove(tag);
    _enablePositionUpdate = _listners.isNotEmpty;
  }
}
