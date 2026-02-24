import 'dart:async';
import 'dart:convert';
import 'dart:io';
import 'dart:ui' as ui;

import 'package:flutter/foundation.dart';
import 'package:flutter/gestures.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_polyline_points/flutter_polyline_points.dart';
import 'package:geocoding/geocoding.dart' as geocoding;
import 'package:geolocator/geolocator.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import 'package:http/http.dart' as http;

import '../../data_utils/utils/result.dart';
import '../../utils/collections/pairs.dart';
import '../../utils/collections/string_set.dart';


///https://pub.dev/packages/flutter_polyline_points

class MapWidget extends StatefulWidget {
  final double height;
  final bool enablePinDropping;
  final Function(GeoLocation)? onPinDropped;
  final Function(GeoLocation)? onLocationSelected;
  final LatLng? initialPinLocation;
  final double controllersTopOffset;
  final VoidCallback? onMapInteractionStart;
  final VoidCallback? onMapInteractionEnd;
  final bool showRouteVisualization;
  final Color routeColor;

  //
  final bool showCurrentLocationButton;
  final bool autoMoveToCurrentLocation;
  final void Function(double lat, double lng)? onFocusMovedToCurrentLocation;

  const MapWidget({
    required this.height,
    this.enablePinDropping = false,
    this.onPinDropped,
    this.onLocationSelected,
    this.initialPinLocation,
    this.controllersTopOffset = 60,
    this.onMapInteractionStart,
    this.onMapInteractionEnd,
    this.showRouteVisualization = false,
    this.routeColor = Colors.orange,
    //
    this.showCurrentLocationButton = true,
    this.autoMoveToCurrentLocation = true,
    this.onFocusMovedToCurrentLocation,
    super.key,
  });

  @override
  State<MapWidget> createState() => MapWidgetState();
}

class MapWidgetState extends State<MapWidget>
    with AutomaticKeepAliveClientMixin {
  //TODO XXXXXX<< THIS KEY MUST CHANGE >>XXXXXX
  static const _gak1_1 = 'A';
  static const _gak1_2 = 'I';
  static const _gak1_3 = 'zaSyAsW';
  static const _gak2 = '1MKToKpb';
  static const _gak3 = 'AlwgzL_';
  static const _gak4 = 'ByKs6Cy';
  static const _gak5 = 'TA60njiM'; //this key 'fox-portal';
  static String get _gak =>
      [_gak1_1, _gak1_2, _gak1_3, _gak2, _gak3, _gak4, _gak5].join();

  final _polylinePoints = PolylinePoints(apiKey: _gak);
  GoogleMapController? _controller;
  final Completer<GoogleMapController> _mapController = Completer();

  bool _isEmulator = false;
  final Map<String, BitmapDescriptor> _customIcons = {};
  Set<Marker> _markers = {};

  final Set<Polyline> _polylines = {};
  LatLng? _droppedPinLocation;
  final bool _isDropPinMode = false;

  late final LatLng capitalPoint;


  //---------------------------------------------------------------------------

  @override
  bool get wantKeepAlive => true;

  @override
  void initState() {
    super.initState();

    capitalPoint = LatLng(25.276987, 51.520008);

    _checkIfEmulator();
    _loadCustomIcons();
    //_initializeMarkers();
  }

  void _checkIfEmulator() {
    _isEmulator = !kIsWeb &&
        (Platform.isAndroid &&
            (Platform.environment['ANDROID_EMULATOR'] == '1' ||
                Platform.environment.containsKey('ANDROID_SDK_ROOT')));
  }

  Future<BitmapDescriptor> _resizeMarker(String assetPath, int width) async {
    final ByteData data = await rootBundle.load(assetPath);
    final ui.Codec codec = await ui.instantiateImageCodec(
      data.buffer.asUint8List(),
      targetWidth: width,
    );

    final ui.FrameInfo fi = await codec.getNextFrame();
    final ByteData? resizedData =
    await fi.image.toByteData(format: ui.ImageByteFormat.png);

    return BitmapDescriptor.fromBytes(resizedData!.buffer.asUint8List());
  }


  Future<void> _loadCustomIcons() async {
    final size = 48.0;
    final config = ImageConfiguration(size: Size(size, size));
    debugPrint('pin-icon-size: $size');

    final pinIconPath = 'assets/images/pin_small.png';
    final pinSmallIconPath = 'assets/images/pin_small.png';

    try {

      // _customIcons[MapMarkerType.pickup.toString()] = await BitmapDescriptor
      //         .fromAssetImage(
      //             config, Platform.isAndroid ? pinIconPath : pinSmallIconPath)
      //     .catchError((_) =>
      //         BitmapDescriptor.defaultMarkerWithHue(BitmapDescriptor.hueGreen));
      //
      // _customIcons[MapMarkerType.dropOff.toString()] = await BitmapDescriptor
      //         .fromAssetImage(
      //             config, Platform.isAndroid ? pinIconPath : pinSmallIconPath)
      //     .catchError((_) =>
      //         BitmapDescriptor.defaultMarkerWithHue(BitmapDescriptor.hueRed));
      //
      // _customIcons[MapMarkerType.stop.toString()] = await BitmapDescriptor
      //         .fromAssetImage(
      //             config, Platform.isAndroid ? pinIconPath : pinSmallIconPath)
      //     .catchError((_) =>
      //         BitmapDescriptor.defaultMarkerWithHue(BitmapDescriptor.hueBlue));
      //
      // _customIcons[MapMarkerType.general.toString()] = await BitmapDescriptor
      //         .fromAssetImage(
      //             config, Platform.isAndroid ? pinIconPath : pinSmallIconPath)
      //     .catchError((_) =>
      //         BitmapDescriptor.defaultMarkerWithHue(BitmapDescriptor.hueAzure));


      _customIcons[MapMarkerType.pickup.toString()] =  await _resizeMarker('assets/images/pin_small.png', 100)
          .catchError((_) =>
          BitmapDescriptor.defaultMarkerWithHue(BitmapDescriptor.hueGreen));

      _customIcons[MapMarkerType.dropOff.toString()] =  await _resizeMarker('assets/images/pin_small.png', 100)
          .catchError((_) =>
          BitmapDescriptor.defaultMarkerWithHue(BitmapDescriptor.hueRed));

      _customIcons[MapMarkerType.stop.toString()] =  await _resizeMarker('assets/images/pin_small.png', 100)
          .catchError((_) =>
          BitmapDescriptor.defaultMarkerWithHue(BitmapDescriptor.hueBlue));

      _customIcons[MapMarkerType.general.toString()] =  await _resizeMarker('assets/images/pin_small.png', 100)
          .catchError((_) =>
          BitmapDescriptor.defaultMarkerWithHue(BitmapDescriptor.hueAzure));



    } catch (e) {
      debugPrint('Error loading custom icons: $e');
      // Fallback to default icons
      _customIcons[MapMarkerType.pickup.toString()] =
          BitmapDescriptor.defaultMarkerWithHue(BitmapDescriptor.hueGreen);
      _customIcons[MapMarkerType.dropOff.toString()] =
          BitmapDescriptor.defaultMarkerWithHue(BitmapDescriptor.hueRed);
      _customIcons[MapMarkerType.stop.toString()] =
          BitmapDescriptor.defaultMarkerWithHue(BitmapDescriptor.hueBlue);
      _customIcons[MapMarkerType.general.toString()] =
          BitmapDescriptor.defaultMarkerWithHue(BitmapDescriptor.hueAzure);
    }

    if (mounted) {
      setState(() {});
      if (_droppedPinLocation != null) {
        _addDroppedPin(_droppedPinLocation!);
      }
    }
  }

  void _initializeMarkers() {
    _markers = Set.from(_createDefaultMarkers());

    debugPrint('map._initializeMarkers --> ${widget.initialPinLocation}');

    if (widget.initialPinLocation != null) {
      _droppedPinLocation = widget.initialPinLocation;
      _addDroppedPin(widget.initialPinLocation!);

      Future.delayed(Duration(milliseconds: 500), () {
        moveToLocation(widget.initialPinLocation!);
      });
    }
  }

  Set<Marker> _createDefaultMarkers() {
    return {
      /*Marker(
        markerId: const MarkerId('doha_center'),
        position: capitalPoint,
        icon: BitmapDescriptor.defaultMarkerWithHue(BitmapDescriptor.hueOrange),
        infoWindow: const InfoWindow(
          title: 'Doha City Center',
          snippet: 'Capital of Qatar',
        ),
      ),*/
    };
  }

  //---------------------------------------------------------------------------

  void _addDroppedPin(LatLng location) {
    final icon = _customIcons[MapMarkerType.general.toString()] ??
        BitmapDescriptor.defaultMarkerWithHue(BitmapDescriptor.hueAzure);

    final newMarkers = Set<Marker>.from(_markers)
      ..removeWhere((m) => m.markerId.value == 'dropped_pin')
      ..add(
        Marker(
          markerId: const MarkerId('dropped_pin'),
          position: location,
          icon: icon,
          infoWindow: InfoWindow(
            title: 'Selected Location',
            snippet:
                '${location.latitude.toStringAsFixed(6)}, ${location.longitude.toStringAsFixed(6)}',
          ),
          onTap: () => _showLocationDetails(location),
        ),
      );

    setState(() => _markers = newMarkers);
  }

  void _showLocationDetails(LatLng location) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: Text('Location Details'),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text('Latitude: ${location.latitude.toStringAsFixed(8)}'),
            SizedBox(height: 8),
            Text('Longitude: ${location.longitude.toStringAsFixed(8)}'),
            SizedBox(height: 16),
            Text('You can use this location for pickup/dropoff points.'),
          ],
        ),
        actions: [
          TextButton(
            onPressed: () {
              Clipboard.setData(ClipboardData(
                text: '${location.latitude}, ${location.longitude}',
              ));
              Navigator.of(context).pop();
              ScaffoldMessenger.of(context).showSnackBar(
                SnackBar(content: Text('Coordinates copied to clipboard!')),
              );
            },
            child: Text('Copy'),
          ),
          TextButton(
            onPressed: () => Navigator.of(context).pop(),
            child: Text('Close'),
          ),
        ],
      ),
    );
  }

  //---------------------------------------------------------------------------

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();

    if (widget.autoMoveToCurrentLocation) {
      Future.delayed(Duration(milliseconds: 800), () {
        gotoCurrentLocation();
      });
    }

    _initializeMarkers();
  }

  //---------------------------------------------------------------------------

  @override
  void dispose() {
    debugPrint("🗑️ Disposing MapWidget");
    _controller?.dispose();
    super.dispose();
  }

  //---------------------------------------------------------------------------

  //region Public methods for external control
  Future<void> drawDrivingRoute(List<LatLng> waypoints) async {
    if (waypoints.length < 2) return;

    final origin = PointLatLng(
      waypoints.first.latitude,
      waypoints.first.longitude,
    );
    final destination = PointLatLng(
      waypoints.last.latitude,
      waypoints.last.longitude,
    );
    final intermediates = waypoints
        .sublist(1, waypoints.length - 1)
        .map((p) => PolylineWayPoint(
              location: '${p.latitude},${p.longitude}',
              stopOver: false,
            ))
        .toList();

    try {
      final result = await _polylinePoints.getRouteBetweenCoordinates(
        request: PolylineRequest(
          origin: origin,
          destination: destination,
          mode: TravelMode.driving,
          wayPoints: intermediates,
        ),
      );

      debugPrint('Directions status: ${result.status}');
      debugPrint('Directions error: ${result.errorMessage}');
      debugPrint('Directions points: ${result.points.length}');

      if (!mounted) return;
      if (result.points.isEmpty) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
              content: Text(
                  'Directions failed: ${result.errorMessage ?? result.status ?? "unknown"}')),
        );
        return; // don’t draw straight line here while debugging
      }

      final route =
          result.points.map((p) => LatLng(p.latitude, p.longitude)).toList();
      setState(() {
        _polylines
          ..clear()
          ..add(Polyline(
            polylineId: const PolylineId('main_route'),
            points: route,
            color: widget.routeColor,
            width: 5,
            startCap: Cap.roundCap,
            endCap: Cap.roundCap,
            jointType: JointType.round,
          ));
      });
      _fitCameraToPoints(route);
    } catch (e, st) {
      debugPrint('Directions exception: $e\n$st');
      if (!mounted) return;
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
            content:
                Text('Directions request denied—check API key & restrictions')),
      );
    }
  }

  /// Draw route between multiple points
  void drawRoute(List<LatLng> points) {
    if (points.length < 2 || !widget.showRouteVisualization) return;

    final polyline = Polyline(
      polylineId: PolylineId('main_route'),
      points: points,
      color: widget.routeColor,
      width: 4,
      patterns: [],
      jointType: JointType.round,
      startCap: Cap.roundCap,
      endCap: Cap.roundCap,
    );

    setState(() {
      _polylines.clear();
      _polylines.add(polyline);
    });

    // Fit camera to show all route points
    _fitCameraToPoints(points);
  }

  void _fitCameraToPoints(List<LatLng> points) async {
    if (points.isEmpty || _controller == null) return;

    if (points.length == 1) {
      await _controller!.animateCamera(
        CameraUpdate.newLatLngZoom(points.first, 15.0),
      );
      return;
    }

    double minLat = points.first.latitude;
    double maxLat = points.first.latitude;
    double minLng = points.first.longitude;
    double maxLng = points.first.longitude;

    for (LatLng point in points) {
      minLat = minLat < point.latitude ? minLat : point.latitude;
      maxLat = maxLat > point.latitude ? maxLat : point.latitude;
      minLng = minLng < point.longitude ? minLng : point.longitude;
      maxLng = maxLng > point.longitude ? maxLng : point.longitude;
    }

    final bounds = LatLngBounds(
      southwest: LatLng(minLat, minLng),
      northeast: LatLng(maxLat, maxLng),
    );

    await _controller!.animateCamera(
      CameraUpdate.newLatLngBounds(bounds, 100.0),
    );
  }

  //------------------------------------------

  /// Clear the current route
  void clearRoute() {
    setState(() {
      _polylines.clear();
    });
  }

  /// Add custom marker with specific type
  void addCustomMarker({
    required String markerId,
    required LatLng position,
    required String title,
    String? snippet,
    MapMarkerType markerType = MapMarkerType.general,
    VoidCallback? onTap,
  }) {
    final icon =
        _customIcons[markerType.toString()] ?? BitmapDescriptor.defaultMarker;

    final marker = Marker(
      markerId: MarkerId(markerId),
      position: position,
      icon: icon,
      infoWindow: InfoWindow(
        title: title,
        snippet: snippet,
      ),
      onTap: onTap,
    );

    setState(() {
      _markers.removeWhere((m) => m.markerId.value == markerId);
      _markers.add(marker);
    });
  }

  /// Clear all custom markers (preserves default markers)
  void clearCustomMarkers() {
    setState(() {
      _markers.removeWhere((marker) =>
          marker.markerId.value != 'doha_center' &&
          marker.markerId.value != 'dropped_pin');
    });
  }

  /// Remove specific marker by ID
  void removeMarker(String markerId) {
    setState(() {
      _markers.removeWhere((marker) => marker.markerId.value == markerId);
    });
  }

  void removePoint(double latitude, double longitude) {
    setState(() {
      _markers.removeWhere(
        (m) =>
            m.position.latitude == latitude &&
            m.position.longitude == longitude,
      );
    });
  }

  //------------------------------------------

  LatLng? getDroppedPinLocation() => _droppedPinLocation;

  Set<Marker> getMarkers() => _markers;

  Set<Polyline> getPolylines() => _polylines;

  void dropPinAt(LatLng location) {
    if (!widget.enablePinDropping) return;

    debugPrint("📍 Map tapped at: ${location.latitude}, ${location.longitude}");

    setState(() {
      _droppedPinLocation = location;
      _addDroppedPin(location);
    });

    var l = GeoLocation(
      lng: location.longitude,
      lat: location.latitude,
      name: null,
    );
    widget.onPinDropped?.call(l);
    widget.onLocationSelected?.call(l);
  }

  Future<void> moveToLocation(LatLng location, {double zoom = 15.0}) async {
    await _controller?.animateCamera(
      CameraUpdate.newLatLngZoom(location, zoom),
    );
  }

  //-----------------------------------

  Future<void> gotoCurrentLocation() async {
    try {
      LocationPermission permission = await Geolocator.checkPermission();
      if (permission == LocationPermission.denied ||
          permission == LocationPermission.deniedForever) {
        //todo show warning message, and let user decide to accept or deny
        //
        if (permission == LocationPermission.denied) {
          permission = await Geolocator.requestPermission();
          if (permission == LocationPermission.denied) {
            _showLocationError('Location permissions are denied');
            return;
          }
        }

        //
        if (permission == LocationPermission.deniedForever) {
          _showLocationError('Location permissions are permanently denied');
          return;
        }
      }

      Position position = await Geolocator.getCurrentPosition(
        desiredAccuracy: LocationAccuracy.high,
        timeLimit: Duration(seconds: 10),
      );

      await _controller?.animateCamera(
        CameraUpdate.newLatLngZoom(
          LatLng(position.latitude, position.longitude),
          15.0,
        ),
      );

      widget.onFocusMovedToCurrentLocation?.call(
        position.latitude,
        position.longitude,
      );
    } catch (e) {
      _showLocationError('Could not get current location: ${e.toString()}');
    }
  }

  void _showLocationError(String message) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(message),
        backgroundColor: Colors.red,
        duration: Duration(seconds: 3),
      ),
    );
  }

  //endregion

  //this for the filter#############################3
  void moveToBounds(LatLngBounds bounds, {double padding = 50}) {
    _controller?.animateCamera(
      CameraUpdate.newLatLngBounds(bounds, padding),
    );
  }


  //---------------------------------------------------------------------------

  @override
  Widget build(BuildContext context) {
    super.build(context);

    return Container(
      height: widget.height,
      width: double.infinity,
      decoration: BoxDecoration(
        border: Border.all(color: Colors.white, width: 2),
        color: Colors.blue.withOpacity(0.1),
      ),
      child: Stack(
        children: [
          _isEmulator ? _buildEmulatorFallback() : _buildGoogleMap(),

          //
          if (widget.enablePinDropping) _buildMapControls(),

          //
          if (widget.enablePinDropping && _isDropPinMode)
            _buildDropPinIndicator(),
        ],
      ),
    );
  }

  Widget _buildEmulatorFallback() {
    return Container(
      color: Colors.grey[300],
      child: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(
              Icons.map,
              size: 64,
              color: Colors.grey[600],
            ),
            SizedBox(height: 16),
            Text(
              'Interactive Map View',
              style: TextStyle(
                fontSize: 18,
                fontWeight: FontWeight.bold,
                color: Colors.grey[700],
              ),
            ),
            SizedBox(height: 8),
            Text(
              'Doha, Qatar (${capitalPoint.latitude}, ${capitalPoint.longitude})',
              style: TextStyle(
                fontSize: 14,
                color: Colors.grey[600],
              ),
            ),
            if (widget.enablePinDropping) ...[
              SizedBox(height: 16),
              Container(
                padding: EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                decoration: BoxDecoration(
                  color: Colors.blue[100],
                  borderRadius: BorderRadius.circular(8),
                  border: Border.all(color: Colors.blue[300]!),
                ),
                child: Text(
                  'Pin Dropping Mode: ${_isDropPinMode ? "ON" : "OFF"}',
                  style: TextStyle(
                    fontSize: 12,
                    fontWeight: FontWeight.w500,
                    color: Colors.blue[800],
                  ),
                ),
              ),
            ],
            if (_polylines.isNotEmpty) ...[
              SizedBox(height: 16),
              Container(
                padding: EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                decoration: BoxDecoration(
                  color: widget.routeColor.withOpacity(0.2),
                  borderRadius: BorderRadius.circular(8),
                  border: Border.all(color: widget.routeColor),
                ),
                child: Text(
                  'Route Visualization Active',
                  style: TextStyle(
                    fontSize: 12,
                    fontWeight: FontWeight.w500,
                    color: widget.routeColor.computeLuminance() > 0.5
                        ? Colors.black
                        : Colors.white,
                  ),
                ),
              ),
            ],
            SizedBox(height: 16),
            Container(
              padding: EdgeInsets.symmetric(horizontal: 16, vertical: 8),
              decoration: BoxDecoration(
                color: Colors.orange[100],
                borderRadius: BorderRadius.circular(8),
                border: Border.all(color: Colors.orange[300]!),
              ),
              child: Text(
                'Google Maps not available in emulator.\nTest on physical device for full functionality.',
                textAlign: TextAlign.center,
                style: TextStyle(
                  fontSize: 12,
                  color: Colors.orange[800],
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  //-----------------------------------

  Widget _buildGoogleMap() {
    return SizedBox(
      height: widget.height,
      width: double.infinity,
      child: GoogleMap(
        mapType: MapType.normal,
        initialCameraPosition: CameraPosition(
          target: capitalPoint,
          zoom: 13,
        ),
        markers: _markers,
        polylines: _polylines,
        gestureRecognizers: <Factory<OneSequenceGestureRecognizer>>{
          Factory<TapGestureRecognizer>(() => TapGestureRecognizer()),
          Factory<PanGestureRecognizer>(() => PanGestureRecognizer()),
          Factory<ScaleGestureRecognizer>(() => ScaleGestureRecognizer()),
        },
        myLocationEnabled: true,
        myLocationButtonEnabled: false,
        compassEnabled: true,
        zoomControlsEnabled: false,
        mapToolbarEnabled: false,
        rotateGesturesEnabled: true,
        scrollGesturesEnabled: true,
        tiltGesturesEnabled: true,
        zoomGesturesEnabled: true,
        minMaxZoomPreference: const MinMaxZoomPreference(8.0, 20.0),
        liteModeEnabled: false,
        onMapCreated: (GoogleMapController controller) {
          _controller = controller;
          _mapController.complete(controller);
        },
        onTap: (p) => dropPinAt(p),
        onLongPress: (LatLng loc) {
          if (widget.enablePinDropping) dropPinAt(loc);
        },
        onCameraMoveStarted: () => widget.onMapInteractionStart?.call(),
        onCameraIdle: () => widget.onMapInteractionEnd?.call(),
        onCameraMove: (_) => widget.onMapInteractionStart?.call(),
      ),
    );
  }

  //-----------------------------------

  Widget _buildMapControls() {
    return Positioned(
      top: widget.controllersTopOffset,
      right: 6,
      child: Column(
        children: [
          // Pin drop toggle button
          /*_buildControlButton(
            heroTag: "pin_drop_${widget.hashCode}",
            onPressed: _toggleDropPinMode,
            backgroundColor: _isDropPinMode ? Colors.red : Colors.white,
            icon: _isDropPinMode ? Icons.push_pin : Icons.push_pin_outlined,
            iconColor: _isDropPinMode ? Colors.white : Colors.red,
            tooltip:
                _isDropPinMode ? 'Disable pin dropping' : 'Enable pin dropping',
          ),*/

          SizedBox(height: 8),

          // Zoom controls
          _buildZoomControls(),

          SizedBox(height: 8),

          // Clear pin button
          /*if (_droppedPinLocation != null)
            _buildControlButton(
              heroTag: "clear_pin_${widget.hashCode}",
              onPressed: _clearDroppedPin,
              backgroundColor: Colors.white,
              icon: Icons.clear,
              iconColor: Colors.orange,
              tooltip: 'Clear dropped pin',
            ),

          if (_droppedPinLocation != null) SizedBox(height: 8),*/

          // Current location button
          if (widget.showCurrentLocationButton)
            _buildControlButton(
              heroTag: "current_location_${widget.hashCode}",
              onPressed: gotoCurrentLocation,
              backgroundColor: Colors.white,
              icon: Icons.my_location,
              iconColor: Colors.black,
              tooltip: 'Go to current location',
            ),
        ],
      ),
    );
  }

  Widget _buildControlButton({
    required String heroTag,
    required VoidCallback onPressed,
    required Color backgroundColor,
    required IconData icon,
    required Color iconColor,
    required String tooltip,
  }) {
    return FloatingActionButton.small(
      heroTag: heroTag,
      onPressed: onPressed,
      backgroundColor: backgroundColor,
      elevation: 4,
      child: Icon(icon, color: iconColor),
      tooltip: tooltip,
    );
  }

  Widget _buildZoomControls() {
    return Container(
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(8),
        boxShadow: [
          BoxShadow(
            color: Colors.black26,
            blurRadius: 4,
            offset: Offset(0, 2),
          ),
        ],
      ),
      child: Column(
        children: [
          InkWell(
            onTap: () async {
              await _controller?.animateCamera(CameraUpdate.zoomIn());
            },
            child: Container(
              padding: EdgeInsets.all(8),
              child: Icon(Icons.add, size: 20),
            ),
          ),
          Container(height: 1, color: Colors.grey[300]),
          InkWell(
            onTap: () async {
              await _controller?.animateCamera(CameraUpdate.zoomOut());
            },
            child: Container(
              padding: EdgeInsets.all(8),
              child: Icon(Icons.remove, size: 20),
            ),
          ),
        ],
      ),
    );
  }

  //-----------------------------------

  Widget _buildDropPinIndicator() {
    return Positioned(
      top: 16,
      left: 16,
      child: Container(
        padding: EdgeInsets.symmetric(horizontal: 12, vertical: 8),
        decoration: BoxDecoration(
          color: Colors.red.withOpacity(0.9),
          borderRadius: BorderRadius.circular(20),
        ),
        child: Row(
          mainAxisSize: MainAxisSize.min,
          children: [
            Icon(Icons.push_pin, color: Colors.white, size: 16),
            SizedBox(width: 4),
            Text(
              'Tap to drop pin',
              style: TextStyle(
                color: Colors.white,
                fontSize: 12,
                fontWeight: FontWeight.w500,
              ),
            ),
          ],
        ),
      ),
    );
  }
}

enum MapMarkerType {
  pickup,
  dropOff,
  stop,
  general,
}

class GeoLocation {
  num? lng;
  num? lat;
  String? _name;
  String? more;

  GeoLocation({
    required this.lng,
    required this.lat,
    required String? name,
    this.more,
  }) : _name = name;

  GeoLocation.non();

  String? get name => _name?.isNotEmpty == true ? _name : null;

  String get alterName => '($lat,$lng)';

  @override
  String toString() {
    return 'GeoLocation{lng: $lng, lat: $lat, name: $_name, more: $more}';
  }
}

///=============================================================================

//region Google Places API
/*https://developers.google.com/maps/documentation/places/web-service/text-search*/
class GooglePlacesApi {
  /*
  "locationBias": {
    "circle": {
      "center": {"latitude": 37.7937, "longitude": -122.3965},
      "radius": 500.0
    }
  }
 */

  Future<Result<List<GeoLocation>>> call({
    //required String apiKey,
    required String query,
  }) async {
    var url = Uri.parse('https://places.googleapis.com/v1/places:searchText');
    
    try {
      final response = await http.post(
        url,
        headers: {
          "Content-Type": 'application/json',
          'X-Goog-Api-Key': MapWidgetState._gak,
          //'X-Goog-FieldMask': '*',
          'X-Goog-FieldMask': 'places.id,places.name,'
              'places.formattedAddress,places.googleMapsUri,places.location',
        },
        body: jsonEncode({"textQuery": query}),
      );
      
      if (response.statusCode >= 200) {
        final data = jsonDecode(response.body);
        List<GeoLocation> lst = [];

        data['places']?.forEach((place) {
          var x = GeoLocation(
            lng: place['location']['longitude'],
            lat: place['location']['latitude'],
            name: place['formattedAddress'],
          );
          lst.add(x);
        });
        
        return Result(lst);
      }
      //
      else {
        return Result(
          null,
          message: StringSet("Can't retrieve data"),
        );
      }
    } catch (e) {
      return Result(null, message: StringSet("Error: $e"));
    }
  }
}

class GooglePlace {
  final String? id;
  final String? formattedAddress;

  ///location: { "latitude":52.615201199999994, "longitude":4.7589657 }
  final Pair<double, double>? latLng;
  final String? googleMapsUri;

  GooglePlace({
    required this.id,
    required this.formattedAddress,
    required this.latLng,
    required this.googleMapsUri,
  });

/*
{
  "places": [
    {
      "name": "places/ChIJcaIlQLNXz0cRT4lPEGj4FPw",
      "id": "ChIJcaIlQLNXz0cRT4lPEGj4FPw",
      "types": [
        "point_of_interest",
        "establishment"
      ],
      "nationalPhoneNumber": "072 527 9100",
      "internationalPhoneNumber": "+31 72 527 9100",
      "formattedAddress": "Robijnstraat 6, 1812 RB Alkmaar, Netherlands",
      "addressComponents": [
        {
          "longText": "6",
          "shortText": "6",
          "types": [
            "street_number"
          ],
          "languageCode": "en-US"
        },
        {
          "longText": "Robijnstraat",
          "shortText": "Robijnstraat",
          "types": [
            "route"
          ],
          "languageCode": "nl"
        },
        {
          "longText": "Alkmaar",
          "shortText": "Alkmaar",
          "types": [
            "locality",
            "political"
          ],
          "languageCode": "nl"
        },
        {
          "longText": "Alkmaar",
          "shortText": "Alkmaar",
          "types": [
            "administrative_area_level_2",
            "political"
          ],
          "languageCode": "nl"
        },
        {
          "longText": "Noord-Holland",
          "shortText": "NH",
          "types": [
            "administrative_area_level_1",
            "political"
          ],
          "languageCode": "nl"
        },
        {
          "longText": "Netherlands",
          "shortText": "NL",
          "types": [
            "country",
            "political"
          ],
          "languageCode": "en"
        },
        {
          "longText": "1812 RB",
          "shortText": "1812 RB",
          "types": [
            "postal_code"
          ],
          "languageCode": "en-US"
        }
      ],
      "plusCode": {
        "globalCode": "9F46JQ85+3H",
        "compoundCode": "JQ85+3H Alkmaar, Netherlands"
      },
      "location": {
        "latitude": 52.615201199999994,
        "longitude": 4.7589657
      },
      "viewport": {
        "low": {
          "latitude": 52.613900319708506,
          "longitude": 4.7576873197084986
        },
        "high": {
          "latitude": 52.6165982802915,
          "longitude": 4.7603852802915032
        }
      },
      "rating": 4.6,
      "googleMapsUri": "https://maps.google.com/?cid=18164416322925988175&g_mp=Cidnb29nbGUubWFwcy5wbGFjZXMudjEuUGxhY2VzLlNlYXJjaFRleHQQAhgEIAA",
      "websiteUri": "https://www.dokh.nl/",
      "regularOpeningHours": {
        "openNow": true,
        "periods": [
          {
            "open": {
              "day": 1,
              "hour": 8,
              "minute": 30
            },
            "close": {
              "day": 1,
              "hour": 16,
              "minute": 30
            }
          },
          {
            "open": {
              "day": 2,
              "hour": 8,
              "minute": 30
            },
            "close": {
              "day": 2,
              "hour": 16,
              "minute": 30
            }
          },
          {
            "open": {
              "day": 3,
              "hour": 8,
              "minute": 30
            },
            "close": {
              "day": 3,
              "hour": 16,
              "minute": 30
            }
          },
          {
            "open": {
              "day": 4,
              "hour": 8,
              "minute": 30
            },
            "close": {
              "day": 4,
              "hour": 16,
              "minute": 30
            }
          },
          {
            "open": {
              "day": 5,
              "hour": 8,
              "minute": 30
            },
            "close": {
              "day": 5,
              "hour": 16,
              "minute": 30
            }
          }
        ],
        "weekdayDescriptions": [
          "Monday: 8:30 AM – 4:30 PM",
          "Tuesday: 8:30 AM – 4:30 PM",
          "Wednesday: 8:30 AM – 4:30 PM",
          "Thursday: 8:30 AM – 4:30 PM",
          "Friday: 8:30 AM – 4:30 PM",
          "Saturday: Closed",
          "Sunday: Closed"
        ],
        "nextCloseTime": "2025-09-18T14:30:00Z"
      },
      "utcOffsetMinutes": 120,
      "adrFormatAddress": "\u003cspan class=\"street-address\"\u003eRobijnstraat 6\u003c/span\u003e, \u003cspan class=\"postal-code\"\u003e1812 RB\u003c/span\u003e \u003cspan class=\"locality\"\u003eAlkmaar\u003c/span\u003e, \u003cspan class=\"country-name\"\u003eNetherlands\u003c/span\u003e",
      "businessStatus": "OPERATIONAL",
      "userRatingCount": 48,
      "iconMaskBaseUri": "https://maps.gstatic.com/mapfiles/place_api/icons/v2/school_pinlet",
      "iconBackgroundColor": "#7B9EB0",
      "displayName": {
        "text": "Stichting DOKh",
        "languageCode": "nl"
      },
      "currentOpeningHours": {
        "periods": [
          {
            "open": {
              "day": 1,
              "hour": 8,
              "minute": 30,
              "date": {
                "year": 2025,
                "month": 9,
                "day": 22
              }
            },
            "close": {
              "day": 1,
              "hour": 16,
              "minute": 30,
              "date": {
                "year": 2025,
                "month": 9,
                "day": 22
              }
            }
          },
          {
            "open": {
              "day": 2,
              "hour": 8,
              "minute": 30,
              "date": {
                "year": 2025,
                "month": 9,
                "day": 23
              }
            },
            "close": {
              "day": 2,
              "hour": 16,
              "minute": 30,
              "date": {
                "year": 2025,
                "month": 9,
                "day": 23
               }
            }
          },
          {
            "open": {
              "day": 3,
              "hour": 8,
              "minute": 30,
              "date": {
                "year": 2025,
                "month": 9,
                "day": 24
              }
            },
            "close": {
              "day": 3,
              "hour": 16,
              "minute": 30,
              "date": {
                "year": 2025,
                "month": 9,
                "day": 24
              }
            }
          },
          {
            "open": {
              "day": 4,
              "hour": 8,
              "minute": 30,
              "date": {
                "year": 2025,
                "month": 9,
                "day": 18
              }
            },
            "close": {
              "day": 4,
              "hour": 16,
              "minute": 30,
              "date": {
                "year": 2025,
                "month": 9,
                "day": 18
              }
            }
          },
          {
            "open": {
              "day": 5,
              "hour": 8,
              "minute": 30,
              "date": {
                "year": 2025,
                "month": 9,
                "day": 19
              }
            },
            "close": {
              "day": 5,
              "hour": 16,
              "minute": 30,
              "date": {
                "year": 2025,
                "month": 9,
                "day": 19
              }
            }
          }
        ],
        "weekdayDescriptions": [
          "Monday: 8:30 AM – 4:30 PM",
          "Tuesday: 8:30 AM – 4:30 PM",
          "Wednesday: 8:30 AM – 4:30 PM",
          "Thursday: 8:30 AM – 4:30 PM",
          "Friday: 8:30 AM – 4:30 PM",
          "Saturday: Closed",
          "Sunday: Closed"
        ],
        "nextCloseTime": "2025-09-18T14:30:00Z"
      },
      "shortFormattedAddress": "Robijnstraat 6","ews": [
        {
          "name": "places/ChIJcaIlQLNXz0cRT4lPEGj4FPw/reviews/ChZDSUhNMG9nS0VJQ0FnSUR0Mk5hbUN3EAE",
          "relativePublishTimeDescription": "a year ago",
          "rating": 5,
          "text": {
            "text": "Good refresh . Motivational and enthusiastic teacher. Highly recommend",
            "languageCode": "en"
          },
          "originalText": {
            "text": "Good refresh . Motivational and enthusiastic teacher. Highly recommend",
            "languageCode": "en"
          },
          "authorAttribution": {
            "displayName": "Stephanie Dahan",
            "uri": "https://www.google.com/maps/contrib/101658774366043240296/reviews",
            "photoUri": "https://lh3.googleusercontent.com/a/ACg8ocIvsZy3Aha30EakJPv4OEkpgESLC8bzQpIZzRW5-8VpJ32cFA=s128-c0x00000000-cc-rp-mo"
          },
          "publishTime": "2024-02-02T10:45:10.654119Z",
          "flagContentUri": "https://www.google.com/local/review/rap/report?postId=ChZDSUhNMG9nS0VJQ0FnSUR0Mk5hbUN3EAE&d=17924085&t=1",
          "googleMapsUri": "https://www.google.com/maps/reviews/data=!4m6!14m5!1m4!2m3!1sChZDSUhNMG9nS0VJQ0FnSUR0Mk5hbUN3EAE!2m1!1s0x47cf57b34025a271:0xfc14f868104f894f"
        },
        {
          "name": "places/ChIJcaIlQLNXz0cRT4lPEGj4FPw/reviews/Ci9DQUlRQUNvZENodHljRjlvT21obGMwTTJTMms1T0dSNlRuQnVRVVpoT0cxWldtYxAB",
          "relativePublishTimeDescription": "a week ago",
          "rating": 1,
          "text": {
            "text": "If your GP has a problem with you, you can file a complaint and then a dispute (€50). Keep in mind that it will take a year, and the dispute resolution will have no connection whatsoever with the dispute you filed. The dispute committee is unaware of the medical world, and they're going to ask your GP about it. Since there's a shortage of GPs, and DOKH trains these types of GPs, they won't dispute it, so it doesn't matter what your doctor does or doesn't do. DOKH always supports your doctor. Agreements made with DOKH aren't kept, or in other words, you don't hear anything at all. Both DOKH and your doctor would rather have you, the patient, disappear into the ether as quickly as possible. The complaint and dispute are a complete charade at DOKH.",
            "languageCode": "en-US"
          },
          "originalText": {
            "text": "als de huisarts een probleem heeft met U dan dien je hier een klacht in en vervolgens een geschil (€50,-) houdt er dan rekening mee dat het een jaar gaat duren en dat de uitspraak van het geschil totaal niet correspondeert met het geschil wat je hebt ingediend en dat de geschillencommissie niet op de hoogten is van de medische wereld en gaat dat dan vragen aan de huisarts??  en aangezien er een tekort is aan huisartsen en dat DOKH ook dit soort huisartsen opleidt gaan ze dit ook niet tegen spreken dus het maakt niet uit wat de arts doet of niet doet DOKH blijft altijd achter de arts staan\nafspraken wat je met DOKH afs preek dat wordt niet nagekomen of te wel je hoort helemaal niets meer dus DOKH en de arts heeft liever dat je als patiënt maar zo snel mogelijk in je urn kruipt en de klacht en geschil is bij DOKH een grote poppenkast",
            "languageCode": "nl"
          },
          "authorAttribution": {
            "displayName": "m f",
            "uri": "https://www.google.com/maps/contrib/109764386943267829083/reviews",
            "photoUri": "https://lh3.googleusercontent.com/a/ACg8ocLYVv86Nqy35AvikaoVHUAvUWhw8dglgEc0-Uz8wHNdzYm7fA=s128-c0x00000000-cc-rp-mo"
          },
          "publishTime": "2025-09-10T10:33:29.766230806Z",
          "flagContentUri": "https://www.google.com/local/review/rap/report?postId=Ci9DQUlRQUNvZENodHljRjlvT21obGMwTTJTMms1T0dSNlRuQnVRVVpoT0cxWldtYxAB&d=17924085&t=1",
          "googleMapsUri": "https://www.google.com/maps/reviews/data=!4m6!14m5!1m4!2m3!1sCi9DQUlRQUNvZENodHljRjlvT21obGMwTTJTMms1T0dSNlRuQnVRVVpoT0cxWldtYxAB!2m1!1s0x47cf57b34025a271:0xfc14f868104f894f"
        },
        {
          "name": "places/ChIJcaIlQLNXz0cRT4lPEGj4FPw/reviews/ChZDSUhNMG9nS0VJQ0FnSUNEd3FqU1ZREAE",
          "relativePublishTimeDescription": "4 months ago",
          "rating": 1,
          "text": {
            "text": "DokH has been holding up cases for 4 months now, nothing has
            come to fruition. not a single appointment is followed, everything
            has to be followed up, they are mainly for the GP, not for patients.
            Mediation is not mediation but a platform to allow the affiliated
            GP to make an argument without any intervention from the so-called
            mediator. very poor complaints handling, nothing. no help
            whatsoever. only false promises that have not come to fruition
            so far.\n\nOn March 26, I received an angry call from DokH
            telling me that I had to delete the review, otherwise no
            help would be given. However, DokH has done nothing, now they
            suddenly know where to find me, everything has been aggravated
            and hindered by DokH, no results whatsoever in 4 months, but
            that doesn't seem to be important to DokH. in the future, a lawyer
            immediately. DokH is there for the GP and unfairly protects them
            and gives them a one-sided unfair platform. Now despite everything
            it has become more difficult due to DokH, still go to a lawyer.\n\n
            in response to the owner's response, DokH has acknowledged that
            the mediation has gone completely wrong by the DokH mediator,
            so that is not just my experience, DokH has no decisiveness
            whatsoever and does nothing at all except to keep things wet!!\n\n
            02-05-2024 DokH, the designated complaints organization, is now
            blackmailing me to delete this review and refusing to explain to
            me where I can file my complaint about the DokH complaints
            organization elsewhere. I've had a lot of things going on my head
            after this review, I can imagine that people are afraid to complain about the DokH complaints organization. and where else can you go? apparently nowhere.\n\nAfter 2 years of opposition, the DokH dispute body made a decision on April 18, 2025 and yielded nothing, on no point, and that is not possible. Unless evidence is omitted and ignored, facts and measurement results are distorted despite previous reports, the paying member at DokH is not at fault whatsoever according to DokH. DokH is not there for the patient, only for their paying members. to be continued..",
            "languageCode": "en-US"
          },
          "originalText": {
            "text": "al 4 maanden houd DokH zaken op, niets komt terecht. geen enkele afspraak word opgevolgd, moet overal achter aan zitten, zijn er vooral voor de huisarts niet voor patiënten. bemiddeling is geen bemiddeling maar een platform om de aangesloten huisarts een betoog te laten houden zonder enig interventie van de zo genoemde bemiddelaar. zeer slecht klachten behandeling, niets. geen enkele hulp. enkel valse beloftes waar niets van terecht komt tot op heden.\n\n26-03 word ik boos gebeld door DokH dat ik de review moet verwijderen, anders word er geen hulp gegeven. echter DokH heeft niets gedaan, nu weten ze mij opeens wel te vinden, alles is verergerd en tegen gewerkt dor DokH, geen enkel resultaat in 4 maanden, maar dat schijnt niet belangrijk te zijn voor DokH. in vervolg meteen een advocaat. DokH is er voor de huisarts en neemt deze onterecht in bescherming en geeft deze een eenzijdig oneerlijk platform. nu ondanks alle moeilijker is geworden door DokH alsnog naar een advocaat.\n\nin reactie op de reactie van de eigenaar, DokH heeft erkent dat de bemiddeling totaal de mist in is gegaan door de bemiddelaar van DokH, dat is dus niet alleen mijn ervaring, DokH heeft geen enkele daadkracht en doet niets helemaal niets behalve pappen en nat houden!!\n\n02-05-2024 DokH, de aangewezen klachten organisatie chanteerd me nu om deze review te verwijderen en weigeren mij uit te leggen waar ik elders mijn klacht over de DokH klachten organisatie aanhanging kan maken. ik heb van alles naar mn hoofd gekregen na deze review, ik kan me voorstellen dat mensen bang zijn om  te klagen over de DokH klachten organisatie. en waar kan je nog terecht? bijkbaar nergens.\n\nde DokH geschillen instantie heeft na 2 jaar van tegenwerking, d.d. 18-4-2025 een uitspraak gedaan en niets opgeleverd, op geen enkel punt, en dat kan niet. Behalve als bewijzen worden weg gelaten en genegeerd, feiten en meetresultaten worden verdraaid ondanks eerdere verslagen, het betalende lid bij DokH treft geen enkele schuld volgens DokH. DokH is er niet voor de patiënt enkel voor hun betalende leden. word vervolgd..",
            "languageCode": "nl"
          },
          "authorAttribution": {
            "displayName": "M E",
            "uri": "https://www.google.com/maps/contrib/100115629216953278278/reviews",
            "photoUri": "https://lh3.googleusercontent.com/a/ACg8ocIP3xRgYGa8rXM5Y1Qo2062U5L1VdC3vh1ckzACqL_rz5ixaw=s128-c0x00000000-cc-rp-mo-ba3"
          },
          "publishTime": "2025-04-23T06:55:19.023956Z",
          "flagContentUri": "https://www.google.com/local/review/rap/report?postId=ChZDSUhNMG9nS0VJQ0FnSUNEd3FqU1ZREAE&d=17924085&t=1",
          "googleMapsUri": "https://www.google.com/maps/reviews/data=!4m6!14m5!1m4!2m3!1sChZDSUhNMG9nS0VJQ0FnSUNEd3FqU1ZREAE!2m1!1s0x47cf57b34025a271:0xfc14f868104f894f"
        },
        {
          "name": "places/ChIJcaIlQLNXz0cRT4lPEGj4FPw/reviews/ChZDSUhNMG9nS0VMbTVsWnJ1NllqaGJREAE",
          "relativePublishTimeDescription": "4 months ago",
          "rating": 5,
          "text": {
            "text": "Super informative and pleasant training with very skilled teachers and actors!!!",
            "languageCode": "en-US"
          },
          "originalText": {
            "text": "Super informatieve en prettige scholing met hele bekwame docenten en acteur!!!",
            "languageCode": "nl"
          },
          "authorAttribution": {
            "displayName": "Sandra Lelieveldt",
            "uri": "https://www.google.com/maps/contrib/104742407829499627803/reviews",
            "photoUri": "https://lh3.googleusercontent.com/a/ACg8ocLFHOYJrNSG3RO_jRtYU1C77RN5iyeLta85C9dmcwCHNSZT3Q=s128-c0x00000000-cc-rp-mo"
          },
          "publishTime": "2025-05-09T12:09:03.631875Z",
          "flagContentUri": "https://www.google.com/local/review/rap/report?postId=ChZDSUhNMG9nS0VMbTVsWnJ1NllqaGJREAE&d=17924085&t=1",
          "googleMapsUri": "https://www.google.com/maps/reviews/data=!4m6!14m5!1m4!2m3!1sChZDSUhNMG9nS0VMbTVsWnJ1NllqaGJREAE!2m1!1s0x47cf57b34025a271:0xfc14f868104f894f"
        },
        {
          "name": "places/ChIJcaIlQLNXz0cRT4lPEGj4FPw/reviews/ChdDSUhNMG9nS0VJQ0FnSURYX05MaTJnRRAB",
          "relativePublishTimeDescription": "10 months ago",
          "rating": 5,
          "text": {
            "text": "I received fantastic help! I was about to give up because it was such a long process, but my contact person at DOKH was very dedicated and really pulled me through! We are very grateful to her!",
            "languageCode": "en-US"
          },
          "originalText": {
            "text": "Ik ben fantastisch geholpen! Ik wilde bijna opgeven omdat het een lange zaak was , maar mijn contactpersoon van DOKH was zeer betrokken en heeft me echt er doorheen gesleept! Wij zijn haar erg dankbaar!",
            "languageCode": "nl"
          },
          "authorAttribution": {
            "displayName": "Sjoukje Broks",
            "uri": "https://www.google.com/maps/contrib/113729885990361212223/reviews",
            "photoUri": "https://lh3.googleusercontent.com/a-/ALV-UjVpOhoWFRQOGw3pvjRCKIaTo6tFehQY9sQntdCbXix9k0aauW8E8w=s128-c0x00000000-cc-rp-mo"
          },
          "publishTime": "2024-10-25T06:34:34.682695Z",
          "flagContentUri": "https://www.google.com/local/review/rap/report?postId=ChdDSUhNMG9nS0VJQ0FnSURYX05MaTJnRRAB&d=17924085&t=1",
          "googleMapsUri": "https://www.google.com/maps/reviews/data=!4m6!14m5!1m4!2m3!1sChdDSUhNMG9nS0VJQ0FnSURYX05MaTJnRRAB!2m1!1s0x47cf57b34025a271:0xfc14f868104f894f"
        }
      ],
      "photos": [
        {
          "name": "places/ChIJcaIlQLNXz0cRT4lPEGj4FPw/photos/AciIO2c3bS8ALHoMzYnuHZw1rsTzMQTvNbC6dORIAnJnJT3vEnoWG-iPEQ5M0e_Y4hwb_AHgAni7n0oRdQs9zpSo71gPGbLcoMwoCzKUgcG7MprCKONEuTY6eJOaTWYfVAj4mfRBa-AxgKlAZoepZw_I5VtwYHSKv5BedgaDQr6A24YaG1l8467_trG79rLTfwnDrgp62LiavoZaTJRa-A4KPwWxNCNo0x5KEPtaDgUi9qOwvl4kC_35AaJJ5WTYPas3HHB-Iz_vT4yNl0iVrYslEWlunlzPBW8Frxt3oYhKUyx42w",
          "widthPx": 800,
          "heightPx": 600,
          "authorAttributions": [
            {
              "displayName": "Stichting DOKh",
              "uri": "https://maps.google.com/maps/contrib/111076378172416391612",
              "photoUri": "https://lh3.googleusercontent.com/a-/ALV-UjXlyqyTeSqKn56YDssDJ59iL0mLrhJd4WdY2MjJsUahGRsjvAw=s100-p-k-no-mo"
            }
          ],
          "flagContentUri": "https://www.google.com/local/imagery/report/?cb_client=maps_api_places.places_api&image_key=!1e10!2sAF1QipPIFMPpV-66LkA3Jw3R0o3L6ywDehD2ttLlExbm&hl=en-US",
          "googleMapsUri": "https://www.google.com/maps/place//data=!3m4!1e2!3m2!1sAF1QipPIFMPpV-66LkA3Jw3R0o3L6ywDehD2ttLlExbm!2e10!4m2!3m1!1s0x47cf57b34025a271:0xfc14f868104f894f"
        },
        {
          "name": "places/ChIJcaIlQLNXz0cRT4lPEGj4FPw/photos/AciIO2eEtWMnMrOg-SXMAmIrac57GUSlnPzTMYVWRue38lnhZ6mPH2xEA5WUnqHJ54ifbHsw0fFPfIqigMhceuXIzLiwGwEmJrDiaDtlR2sDcRbZOEyrwL7L_yyb134IFZt4dtWDERsZWZIDxP_GlGfwk4_5G21QdZHdMzfEsLhwuRph7UZcPBqpgYCAMQ56VT7jWBANliV5_2Eg49sS_vZVKY3uOdVmIHjKD5z_1529lx5owo7hMMZtyM1Ju7zwndihF6GHia3yt916Tv600C7PyCUXHmo2QWz0omsC-bJbX5oeFw",
          "widthPx": 800,
          "heightPx": 600,
          "authorAttributions": [
            {
              "displayName": "Stichting DOKh",
              "uri": "https://maps.google.com/maps/contrib/111076378172416391612",
              "photoUri": "https://lh3.googleusercontent.com/a-/ALV-UjXlyqyTeSqKn56YDssDJ59iL0mLrhJd4WdY2MjJsUahGRsjvAw=s100-p-k-no-mo"
            }
          ],
          "flagContentUri": "https://www.google.com/local/imagery/report/?cb_client=maps_api_places.places_api&image_key=!1e10!2sAF1QipNlnvQk3Xlz0czhIzB0o7Qd2IytAd0yRPPgraPR&hl=en-US",
          "googleMapsUri": "https://www.google.com/maps/place//data=!3m4!1e2!3m2!1sAF1QipNlnvQk3Xlz0czhIzB0o7Qd2IytAd0yRPPgraPR!2e10!4m2!3m1!1s0x47cf57b34025a271:0xfc14f868104f894f"
        },
        {
          "name": "places/ChIJcaIlQLNXz0cRT4lPEGj4FPw/photos/AciIO2frIvsGM3jlJEzv_hfvV479c2MxEL2O7kb0LBo1z2qCeDlEZE-5xKPXDXKllro8kX9IYyyUvvY8nl3XiEteQPKDh245Is8nHZBOGLTUu7FarfBIIOhA7QqqNdGur3xpNXTCss0rXdUAedSkYCNUpwauyChT-HP3ylrw9nnkldcgTqXhPJekK2W6YprbxkcB0G1K5jZDa6Owhe_jnfOICya3qE5gQCMRE_XgmNsNqC5K1n-kxOsHkfxT9ec90WsjWGX0LSQpISGQncmkTlIa3fGC1YbTVMplE4F9UwMHhqZpmg",
          "widthPx": 600,
          "heightPx": 800,
          "authorAttributions": [
            {
              "displayName": "Stichting DOKh",
              "uri": "https://maps.google.com/maps/contrib/111076378172416391612",
              "photoUri": "https://lh3.googleusercontent.com/a-/ALV-UjXlyqyTeSqKn56YDssDJ59iL0mLrhJd4WdY2MjJsUahGRsjvAw=s100-p-k-no-mo"
            }
          ],
          "flagContentUri": "https://www.google.com/local/imagery/report/?cb_client=maps_api_places.places_api&image_key=!1e10!2sAF1QipMwU2ZOU7uEj-rawjG1Cl1jFpXTvdIaKxMhj-Ji&hl=en-US",
          "googleMapsUri": "https://www.google.com/maps/place//data=!3m4!1e2!3m2!1sAF1QipMwU2ZOU7uEj-rawjG1Cl1jFpXTvdIaKxMhj-Ji!2e10!4m2!3m1!1s0x47cf57b34025a271:0xfc14f868104f894f"
        },
        {
          "name": "places/ChIJcaIlQLNXz0cRT4lPEGj4FPw/photos/AciIO2frPci59CM2dFm8exKQJuyfy0KrOgKmOWBtM6DDWZI9lHwZ-BHfj6L9WaQspzncKt3Usqo5Um-tSUYhvT03ktcmTnSkciflUbh0r3DLz43OegfeZ2yE_YgtW0uHreAL2OqIIYxYmKc9C-RAmzhGDH02IC0y75c6Ndfsc_bLae_HQKDi1zrLIeHAaPdI-O5HlqpbAMyU-RKwv3YN39bgNu_qifDB5HfNjltaBIR4D_zEo0UAZCm0QELfbmwogs_yd2M4TZH7uo8UxqVYxpEuiVWwOcAz5aj4WAPoySXux9l55g",
           "widthPx": 3005,
          "heightPx": 4250,
          "authorAttributions": [
            {
              "displayName": "Stichting DOKh",
              "uri": "https://maps.google.com/maps/contrib/111076378172416391612",
              "photoUri": "https://lh3.googleusercontent.com/a-/ALV-UjXlyqyTeSqKn56YDssDJ59iL0mLrhJd4WdY2MjJsUahGRsjvAw=s100-p-k-no-mo"
            }
          ],
          "flagContentUri": "https://www.google.com/local/imagery/report/?cb_client=maps_api_places.places_api&image_key=!1e10!2sAF1QipNMD7CSk_InVCVYHGm4EOow6olqK74zDP4S_dRD&hl=en-US",
          "googleMapsUri": "https://www.google.com/maps/place//data=!3m4!1e2!3m2!1sAF1QipNMD7CSk_InVCVYHGm4EOow6olqK74zDP4S_dRD!2e10!4m2!3m1!1s0x47cf57b34025a271:0xfc14f868104f894f"
        }
      ],
      "accessibilityOptions": {
        "wheelchairAccessibleParking": true,
        "wheelchairAccessibleEntrance": true
      },
      "addressDescriptor": {
        "landmarks": [
          {
            "name": "places/ChIJkU7Xei5Wz0cR0H_dTEyxdb8",
            "placeId": "ChIJkU7Xei5Wz0cR0H_dTEyxdb8",
            "displayName": {
              "text": "McDonald's",
              "languageCode": "en"
            },
            "types": [
              "establishment",
              "food",
              "meal_takeaway",
              "point_of_interest",
              "restaurant"
            ],
            "straightLineDistanceMeters": 75.05915,
            "travelDistanceMeters": 133.93727
          },
          {
            "name": "places/ChIJ-wMOIS5Wz0cRm8g6WjXW3Mg",
            "placeId": "ChIJ-wMOIS5Wz0cRm8g6WjXW3Mg",
            "displayName": {
              "text": "KFC",
              "languageCode": "en"
            },
            "types": [
              "establishment",
              "food",
              "meal_delivery",
              "meal_takeaway",
              "point_of_interest",
              "restaurant"
            ],
            "straightLineDistanceMeters": 299.31604,
            "travelDistanceMeters": 557.86
          },
          {
            "name": "places/ChIJDwET7phXz0cRZk4GfrptwXE",
            "placeId": "ChIJDwET7phXz0cRZk4GfrptwXE",
            "displayName": {
              "text": "Smart Business Center Alkmaar",
              "languageCode": "nl"
            },
            "types": [
              "establishment",
              "point_of_interest",
              "real_estate_agency"
            ],
            "straightLineDistanceMeters": 104.47736,
            "travelDistanceMeters": 183.5595
          },
          {
            "name": "places/ChIJcY-2KDBWz0cRgGhRJ6yRHNc",
            "placeId": "ChIJcY-2KDBWz0cRgGhRJ6yRHNc",
            "displayName": {
              "text": "Praxis Bouwmarkt Alkmaar",
              "languageCode": "en"
            },
            "types": [
              "establishment",
              "home_goods_store",
              "point_of_interest",
              "store"
            ],
            "straightLineDistanceMeters": 414.5708,
            "travelDistanceMeters": 590.88293
          },
          {
            "name": "places/ChIJM7EXMyxWz0cR0kn7xq45iJ8",
            "placeId": "ChIJM7EXMyxWz0cR0kn7xq45iJ8",
            "displayName": {
              "text": "Makelaar Alkmaar | VLIEG Makelaars, Hypotheken en Verzekeringen",
              "languageCode": "nl"
            },
            "types": [
              "establishment",
              "finance",
              "insurance_agency",
              "point_of_interest",
              "real_estate_agency"
            ],
            "straightLineDistanceMeters": 143.23524,
            "travelDistanceMeters": 197.47107
          }
        ]
      },
      "googleMapsLinks": {
        "directionsUri": "https://www.google.com/maps/dir//''/data=!4m7!4m6!1m1!4e2!1m2!1m1!1s0x47cf57b34025a271:0xfc14f868104f894f!3e0?g_mp=Cidnb29nbGUubWFwcy5wbGFjZXMudjEuUGxhY2VzLlNlYXJjaFRleHQQAhgEIAA",
        "placeUri": "https://maps.google.com/?cid=18164416322925988175&g_mp=Cidnb29nbGUubWFwcy5wbGFjZXMudjEuUGxhY2VzLlNlYXJjaFRleHQQAhgEIAA",
        "writeAReviewUri": "https://www.google.com/maps/place//data=!4m3!3m2!1s0x47cf57b34025a271:0xfc14f868104f894f!12e1?g_mp=Cidnb29nbGUubWFwcy5wbGFjZXMudjEuUGxhY2VzLlNlYXJjaFRleHQQAhgEIAA",
        "reviewsUri": "https://www.google.com/maps/place//data=!4m4!3m3!1s0x47cf57b34025a271:0xfc14f868104f894f!9m1!1b1?g_mp=Cidnb29nbGUubWFwcy5wbGFjZXMudjEuUGxhY2VzLlNlYXJjaFRleHQQAhgEIAA",
        "photosUri": "https://www.google.com/maps/place//data=!4m3!3m2!1s0x47cf57b34025a271:0xfc14f868104f894f!10e5?g_mp=Cidnb29nbGUubWFwcy5wbGFjZXMudjEuUGxhY2VzLlNlYXJjaFRleHQQAhgEIAA"
      },
      "timeZone": {
        "id": "Europe/Amsterdam"
      },
      "postalAddress": {
        "regionCode": "NL",
        "languageCode": "en-US",
        "postalCode": "1812 RB",
        "locality": "Alkmaar",
        "addressLines": [
          "Robijnstraat 6"
        ]
      }
    }
  ],
  "contextualContents": [
    {
      "photos": [
        {
          "name": "places/ChIJcaIlQLNXz0cRT4lPEGj4FPw/photos/AciIO2fHKZIs4OfAFQmdG9CVpkIkUU9X8-L6q9Wq4jzOaOgreo5XWZpHfjn5Mp9DSLcNa3zGyZe83IxUu15i54Pm3cCPyWh0s5B5aHb9K7dsZbTOlC30uNAjmO3dl53ET7Bwvo-idqskAFKF3U1VHRoZ0G-rdKq_9b_Fv4wZwb9MZo8I_-K9mVT4YHbC7orinb2v1DucfjbtbB-VMjMbrH4z-HzskkumFEUWT2oYu8Z6d2V-ihNYi4yROiwA4DtiFAS1bW8GYc1A5EMxxcFb5ujs-svqxfQGQr74Wg",
          "widthPx": 800,
          "heightPx": 600,
          "authorAttributions": [
            {
              "displayName": "Stichting DOKh",
              "uri": "https://maps.google.com/maps/contrib/111076378172416391612",
              "photoUri": "https://lh3.googleusercontent.com/a-/ALV-UjXlyqyTeSqKn56YDssDJ59iL0mLrhJd4WdY2MjJsUahGRsjvAw=s100-p-k-no-mo"
            }
          ],
          "flagContentUri": "https://www.google.com/local/imagery/report/?cb_client=maps_api_places&image_key=!1e10!2sAF1QipPIFMPpV-66LkA3Jw3R0o3L6ywDehD2ttLlExbm&hl=en-US",
          "googleMapsUri": "https://www.google.com/maps/place//data=!3m4!1e2!3m2!1sAF1QipPIFMPpV-66LkA3Jw3R0o3L6ywDehD2ttLlExbm!2e10!4m2!3m1!1s0x47cf57b34025a271:0xfc14f868104f894f"
        },
        {
          "name": "places/ChIJcaIlQLNXz0cRT4lPEGj4FPw/photos/AciIO2face8Vol6RmLrmLxnekZlUAATUVNPlqkIdAP10eFSgB_tmO98-UjnKWXo9qOmDIJBCYWTGPjooDctYhRQYefrGVuKzMEmzy568KFKnV1r81NIGG18e5hpDs4_WQXyvfkQIQo2_PqC5mlLr5YkJYqcWMF6GFOfPH0Bz-cTLqHr5giITWX26DHLDwFTUt1kOApg28zsguKnO4UmBVR9bjV-jS7ykkh4y-hYTuRrD0Rnto3DCwGYHJj8A4HzbTcXWpUb5RQvO52T6HaAdWCcGGUdQLXIiVLCNyQ",
          "widthPx": 800,
          "heightPx": 600,
          "authorAttributions": [
            {
              "displayName": "Stichting DOKh",
              "uri": "https://maps.google.com/maps/contrib/111076378172416391612",
              "photoUri": "https://lh3.googleusercontent.com/a-/ALV-UjXlyqyTeSqKn56YDssDJ59iL0mLrhJd4WdY2MjJsUahGRsjvAw=s100-p-k-no-mo"
            }
          ],
          "flagContentUri": "https://www.google.com/local/imagery/report/?cb_client=maps_api_places&image_key=!1e10!2sAF1QipNlnvQk3Xlz0czhIzB0o7Qd2IytAd0yRPPgraPR&hl=en-US",
          "googleMapsUri": "https://www.google.com/maps/place//data=!3m4!1e2!3m2!1sAF1QipNlnvQk3Xlz0czhIzB0o7Qd2IytAd0yRPPgraPR!2e10!4m2!3m1!1s0x47cf57b34025a271:0xfc14f868104f894f"
        },
        {
          "name": "places/ChIJcaIlQLNXz0cRT4lPEGj4FPw/photos/AciIO2eKRw2-ze5hWSub9y66Gcin1bSeZGpQWrfy5tD6sXx6UXHxatMo5EEDD725RYJ_r-gSTpXPxfM35wbeaYBzgSDHVFkY3iMuNHpHYEQbaRwgM1mSRgd6j605U8JIHf9asRV_lhiCc3OfLH7OQpBZYfTrcLP0G6fv_NGP3_H4vZj3zczRHwcULZ1LjVtjvp02MFtvCiHSTKf_5oyzIFot0O45SqYnRPFtiJYeYQCK1Gh-nsrWL1p_idiWu_5uNnHnLlXPXn97wJXuDha1Bzushs6vAIKPyZKCuA",
          "widthPx": 600,
          "heightPx": 800,
          "authorAttributions": [
            {
              "displayName": "Stichting DOKh",
              "uri": "https://maps.google.com/maps/contrib/111076378172416391612",
              "photoUri": "https://lh3.googleusercontent.com/a-/ALV-UjXlyqyTeSqKn56YDssDJ59iL0mLrhJd4WdY2MjJsUahGRsjvAw=s100-p-k-no-mo"
            }
          ],
          "flagContentUri": "https://www.google.com/local/imagery/report/?cb_client=maps_api_places&image_key=!1e10!2sAF1QipMwU2ZOU7uEj-rawjG1Cl1jFpXTvdIaKxMhj-Ji&hl=en-US",
          "googleMapsUri": "https://www.google.com/maps/place//data=!3m4!1e2!3m2!1sAF1QipMwU2ZOU7uEj-rawjG1Cl1jFpXTvdIaKxMhj-Ji!2e10!4m2!3m1!1s0x47cf57b34025a271:0xfc14f868104f894f"
        },
        {
          "name": "places/ChIJcaIlQLNXz0cRT4lPEGj4FPw/photos/AciIO2eWtMDFKV5cfJbF_vLytMhkTudUb9f1ZOIjj3iOhwwNR44ooIf8m51suHW-qBBA0gphFw352QCe2vISMy6_qHsdTrffpE_fLaklsWfzYri2344ksc3lJ7Lcra5vixHTMRP0ybHTrtleQyZ7QFxO0d5tP61Eq6jyashSvLabVxVxS5Wfuv2jLajToSuziV3b-oVune7k_GxLXHbdOfVae_EUjRWSWO2AkIBCiqsKL1XqEfRHdAZnSFh9LugAXCnQxionhRxY7b0JBP_p6w9lZXgniIP_szCb9A",
          "widthPx": 3005,
          "heightPx": 4250,
          "authorAttributions": [
            {
              "displayName": "Stichting DOKh",
              "uri": "https://maps.google.com/maps/contrib/111076378172416391612",
              "photoUri": "https://lh3.googleusercontent.com/a-/ALV-UjXlyqyTeSqKn56YDssDJ59iL0mLrhJd4WdY2MjJsUahGRsjvAw=s100-p-k-no-mo"
            }
          ],
          "flagContentUri": "https://www.google.com/local/imagery/report/?cb_client=maps_api_places&image_key=!1e10!2sAF1QipNMD7CSk_InVCVYHGm4EOow6olqK74zDP4S_dRD&hl=en-US",
          "googleMapsUri": "https://www.google.com/maps/place//data=!3m4!1e2!3m2!1sAF1QipNMD7CSk_InVCVYHGm4EOow6olqK74zDP4S_dRD!2e10!4m2!3m1!1s0x47cf57b34025a271:0xfc14f868104f894f"
        }
      ]
    }
  ],
  "searchUri": "https://www.google.com/maps/search/dokh"
}
 */
}
//endregion

///=============================================================================

class GeoCodingHelper {
  static Future<String?> generateLocationDisplayName({
    required double lat,
    required double lng,
    Value<String>? altName,
  }) async {
    try {
      List<geocoding.Placemark> placemarks =
          await geocoding.placemarkFromCoordinates(lat, lng);

      debugPrint('GeoCodingHelper.generateLocationDisplayName('
          'location: {lat: $lat, lng: $lng}'
          ') ----> place: ${placemarks.firstOrNull}');

      if (placemarks.isNotEmpty) {
        geocoding.Placemark place = placemarks.first;

        String address = '';
        if (place.street?.isNotEmpty == true) {
          address += place.street!;
        }
        if (place.locality?.isNotEmpty == true) {
          if (address.isNotEmpty) address += ', ';
          address += place.locality!;
        }
        if (place.administrativeArea?.isNotEmpty == true) {
          if (address.isNotEmpty) address += ', ';
          address += place.administrativeArea!;
        }

        return address;
      }
      //
      else {
        debugPrint('GeoCodingHelper.generateLocationDisplayName('
            'location: {lat: $lat, lng: $lng}'
            ') ----> no places found');
      }
    } catch (e) {
      debugPrint("GeoCodingHelper.generateLocationDisplayName("
          "location: {lat: $lat, lng: $lng}"
          ") ----> "
          "EXCEPTION:: $e");
    }

    return altName?.value ?? "($lat,$lng)";
  }
}

class Value<V> {
  final V? value;

  Value(this.value);
}

///=============================================================================

class BaseMapState<W extends StatefulWidget> extends State<W> {
  final GlobalKey<MapWidgetState> _mapKey = GlobalKey<MapWidgetState>();
  GeoLocation? _selectedLocation;

  @override
  Widget build(BuildContext context) {
    final size = MediaQuery.of(context).size;
    return mapContainer(_map(height: size.height));
  }

  Widget mapContainer(Widget map) {
    return map;
  }

  MapWidgetState? get mapState => _mapKey.currentState;

  GeoLocation? get selectedLocation => _selectedLocation;

  void setLocation(GeoLocation location) {
    _handleLocationSelected(location);
  }

  void removeMapMarker(double latitude, double longitude) {
    final mapWidget = _mapKey.currentState;
    if (mapWidget == null) return;

    mapWidget.removePoint(latitude, longitude);
  }

  void onLocationDropped(GeoLocation location) {}

  //region map widget methods
  Widget _map({required double height}) {
    return MapWidget(
      key: _mapKey,
      height: height,// * 1.3,
      enablePinDropping: true,
      controllersTopOffset: height - 300,
      onPinDropped: _handleLocationSelected,
      showRouteVisualization: true,
      routeColor: Colors.orange,
      //
      showCurrentLocationButton: true,
      autoMoveToCurrentLocation: true,
      onFocusMovedToCurrentLocation: (lat, lng) {
        _handleLocationSelected(GeoLocation(lng: lng, lat: lat, name: null));
      },
    );
  }

  void _handleLocationSelected(GeoLocation location) async {
    final latLng = LatLng(location.lat!.toDouble(), location.lng!.toDouble());
    _selectedLocation = GeoLocation(
      lat: location.lat,
      lng: location.lng,
      name: location.name?.isNotEmpty == true
          ? location.name!
          : (await _generateLocationDisplayName(latLng)),
    );

    _updateMapMarkers();

    onLocationDropped(location);
  }

  Future<String> _generateLocationDisplayName(LatLng location) async {
    return (await GeoCodingHelper.generateLocationDisplayName(
      lat: location.latitude,
      lng: location.longitude,
    ))!;
  }

  void _updateMapMarkers() {
    final mapWidget = _mapKey.currentState;
    if (mapWidget == null) return;

    mapWidget.clearCustomMarkers();

    // Add pickup marker
    if (_selectedLocation != null) {
      final latLng = LatLng(
        _selectedLocation!.lat!.toDouble(),
        _selectedLocation!.lng!.toDouble(),
      );

      mapWidget.addCustomMarker(
        markerId: 'pickup',
        position: latLng,
        title: 'Pickup Location',
        snippet: _selectedLocation!.name,
        markerType: MapMarkerType.pickup,
      );
    }
  }

//endregion
}
