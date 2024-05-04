class PageInfo {
  late final int offset;
  late final int limit;

  PageInfo({required this.offset, required this.limit});

  PageInfo.byPaging({required int pageNumber, required int pageSize}) {
    offset = (pageNumber - 1) * pageSize;
    limit = pageSize;
  }

  @override
  String toString() {
    return '{offset: $offset (page: ${offset / limit + 1}), limit: $limit}';
  }
}
