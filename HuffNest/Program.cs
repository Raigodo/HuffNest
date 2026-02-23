await new HuffNestFile(
    "E:\\Projects\\huffnest\\test.txt",
    "E:\\Projects\\huffnest\\temp.txt"
).Compress(1);

await new HuffNestFile(
    "E:\\Projects\\huffnest\\temp.txt",
    "E:\\Projects\\huffnest\\test2.txt"
).Decompress();
