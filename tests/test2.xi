use io
use conv

main(args: int[][]) {
  str:int[] = "0123456789";
  println(str)
  println(unparseInt(length(str)))
  i:int, b:bool = parseInt(str);
  if b println(unparseInt(i))
}
