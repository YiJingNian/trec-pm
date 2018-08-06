import os
import sys
import zipfile
import gzip
from struct import *

import gensim
from gensim.test.utils import common_texts
from gensim.models.doc2vec import Doc2Vec, TaggedDocument

import time

def infervector(text):
	tokens = list(gensim.utils.tokenize(text, lower=True))
	return model.infer_vector(tokens)

print("Reading doc2vec model...", file=sys.stderr)
model = Doc2Vec.load(sys.argv[1])
vectorSize = model.vector_size

print("Waiting for input on STDIN, one text document batch per line", file=sys.stderr)
for line in sys.stdin:
	alltime = time.time()
	if line.strip() == "quit":
		sys.exit(0)
	classifytime = time.time()
	vector = infervector(line)
	classifytime = time.time() - classifytime
	bytes = pack('>%sd' % len(vector), *vector)
	sys.stdout.buffer.write(pack('>i', len(bytes)))
	sys.stdout.buffer.write(bytes)
	print(end='')
	alltime = time.time() - alltime

	#print("Timing: vector inference time: ", classifytime, file=sys.stderr)
	#print("Timing: allover time: ", alltime, file=sys.stderr)
