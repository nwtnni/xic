use qt
use io

main(origArgs: int[][]) {
    app: QApplication, args: int[][] = qapplication(origArgs)
    a: int = 0

    while (a < length(args)) {
        println(args[a])
        a =  a + 1
    }

    b: QPushButton = qpushbutton (qs("hi"))
    b.show()
    app.exec()
}
