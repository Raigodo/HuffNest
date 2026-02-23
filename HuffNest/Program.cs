await new HuffNestFile(
    "C:\\Users\\valte\\Desktop\\test1\\test1.pdf",
    "C:\\Users\\valte\\Desktop\\test1\\temp"
).Compress(1);

await new HuffNestFile(
    "C:\\Users\\valte\\Desktop\\test1\\temp",
    "C:\\Users\\valte\\Desktop\\test1\\30mb_my.pdf"
).Decompress();
