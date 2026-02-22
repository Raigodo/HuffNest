using (FileStream fs = new FileStream("E:\\Projects\\huffnest\\test.txt", FileMode.Open))
{
    byte[] buffer = new byte[10];
    int bytesRead = fs.Read(buffer, 0, 10);
    for (int i = 0; i < bytesRead; i++)
        Console.WriteLine((char)buffer[i]);
}
