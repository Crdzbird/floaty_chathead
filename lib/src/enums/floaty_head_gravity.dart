/// Set the [gravity] orientation for the header of the chathead
///
/// use [top] to position the content of the header
/// to the upper side of the container.
///
/// use [bottom] to position the content of the header
/// to the bottom side of the container.
///
/// use [center] to position the content of the header
/// to the bottom side of the container.
enum FloatyHeadGravity {
  top(1),
  bottom(-1),
  center(0);

  final int position;
  const FloatyHeadGravity(this.position);
}
