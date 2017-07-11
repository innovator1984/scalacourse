"my".map(ch=>(ch,1))
.groupBy(_._1)
.map(kv => (kv._1,kv._2.map(_._2).sum))
.toList
.sortBy(_._1)
