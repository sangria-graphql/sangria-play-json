import sbt.VirtualAxis.WeakAxis

case class PlayAxis(idSuffix: String, directorySuffix: String) extends WeakAxis {}

object PlayAxis {
  val play28: PlayAxis = PlayAxis("_play28", "_play28")
  val play29: PlayAxis = PlayAxis("_play29", "_play29")
  val play30: PlayAxis = PlayAxis("_play30", "_play30")
}
