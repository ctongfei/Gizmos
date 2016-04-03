package poly.collection

object +: {

  /** Decomposes a sequence into its head and its tail. */
  def unapply[T](t: Seq[T]) = {
    if (t.isEmpty) None
    else Some((t.head, t.tail))
  }
}

object :+ {

  /** Decomposes a sequence into its init and its last. */
  def unapply[T](t: Seq[T]) = {
    if (t.isEmpty) None
    else Some((t.init, t.last))
  }

}

object :|: {

  /** Decomposes an indexed sequence into its first half and its second half. */
  def unapply[T](t: IndexedSeq[T]) = {
    val len = t.length
    val mid = len / 2
    Some(t.slice(0, mid), t.slice(mid, len))
  }
}

object :|< {
  private[poly] class IndexedSeqRightHalfWithMid[+T](val mid: T, val right: IndexedSeq[T])
  def unapply[T](t: IndexedSeq[T]) = {
    val len = t.length
    val mid = len / 2
    Some(t.slice(0, mid), new IndexedSeqRightHalfWithMid[T](t(mid), t.slice(mid + 1, len)))
  }
}

object <|: {
  def unapply[T](t: :|<.IndexedSeqRightHalfWithMid[T]) = Some(t.mid, t.right)
}

object :/ {
  private[poly] class BinaryTreeNodeWithRight[+T](val root: T, val right: BinaryTree[T])
  /**
   * Decomposes a binary tree into its left subtree, its root element and its right subtree.
   * Used in couple with `\:`.
   * @example {{{
   *   binaryTree match {
   *    case (l :/ n \: r) => ...
   *   }
   * }}}
   * The symbols follow the same mnemonics as `:+` and `+:` : ''The COLon side is where the COLlection is''.
   */
  def unapply[T](t: BinaryTree[T]) = {
    Some((t.left, new BinaryTreeNodeWithRight[T](t.root, t.right)))
  }
}

object \: {
  /**
   * Decomposes a binary tree into its left subtree, its root element and its right subtree.
   * Used in couple with `:/`.
   * @example {{{
   *   binaryTree match {
   *    case (l :/ n \: r) => ...
   *   }
   * }}}
   * The symbols follow the same mnemonics as `:+` and `+:` : ''The COLon side is where the COLlection is''.
   */
  def unapply[T](t: :/.BinaryTreeNodeWithRight[T]) = Some(t.root, t.right)

}