using IO;

var br = new BitReader("E:\\Projects\\huffnest\\test.txt");

var bw = new BitWriter("E:\\Projects\\huffnest\\test2.txt");

while (br.HasNextByte)
{
    var value = await br.GetNextByteAsync();
    await bw.WriteByteAsync(value);
}

br.Close();
await bw.CloseAsync();
