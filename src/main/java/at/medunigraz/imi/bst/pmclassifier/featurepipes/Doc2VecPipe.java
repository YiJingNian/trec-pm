package at.medunigraz.imi.bst.pmclassifier.featurepipes;

import cc.mallet.pipe.Pipe;
import cc.mallet.types.Instance;
import cc.mallet.types.Token;
import de.julielab.ipc.javabridge.Options;
import de.julielab.ipc.javabridge.StdioBridge;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.util.Optional;
import java.util.stream.Stream;

public class Doc2VecPipe extends Pipe {

    private final StdioBridge<byte[]> bridge;

    public Doc2VecPipe() {
        Options<byte[]> options = new Options(byte[].class);
        options.setExecutable("python");
        options.setExternalProgramTerminationSignal("quit");
        bridge = new StdioBridge(options, "-u", "scripts/doc2VecInferer.py", "/Users/faessler/Research/trecpm2018/doc2vec/doc2vec_vs300_w15_500k.mod");
        try {
            bridge.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
static int docnum = 0;
    @Override
    public Instance pipe(Instance inst) {
        Token token = (Token) inst.getData();
        String text = token.getText();
        try {
            long time = System.nanoTime();
            Stream<byte[]> stream = bridge.sendAndReceive(text);
            time = System.nanoTime() - time;
            System.out.println("Doc " + docnum++ + ": " + time + " " + text.length());
            Optional<byte[]> any = stream.findAny();
            if (!any.isPresent())
                throw new IllegalStateException("The STDIO bridge to the Gensim Doc2vec program didn't return a value.");
            DoubleBuffer db = ByteBuffer.wrap(any.get()).asDoubleBuffer();
            for (int i = 0; i < db.capacity(); i++) {
                token.setFeatureValue("doc2vec_pos_" + i, db.get(i));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return inst;
    }
}
