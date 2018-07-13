import sys
import json
import pandas as pd
import pickle
import string
from nltk.stem import PorterStemmer
from nltk import word_tokenize
from nltk import download
from sklearn.pipeline import Pipeline
from sklearn.feature_extraction.text import TfidfVectorizer
from nltk.corpus import stopwords
import sklearn
from sklearn import preprocessing

# download punkt package, used for tokenization by nltk
download('punkt')

def stemming_tokenizer(text):
	stemmer = PorterStemmer()
	return [stemmer.stem(w) for w in word_tokenize(text)]

def classify(jsonDict):
	# adds title, abstracts and mesh terms into one document
	X_test = pd.Series(
		[j_dict[str(i)]["title"] + " " + j_dict[str(i)]["abstract"] +
		" " + str(j_dict[str(i)]["major_mesh"] or "") + " " + str(j_dict[str(i)]["minor_mesh"] or "")
		for i in range(len(j_dict))]
	)

	X_test = tfidf.transform(X_test)

	# pm should be 1 for either human or animal pm or 0 for non pm
#	y_test = pd.Series([j_dict[str(i)]["pm"] for i in range(len(j_dict))])
	#Encode from string to numbers
	#enc = preprocessing.LabelEncoder()
#	y_test = enc.fit_transform(y_test)
	y_hat = model.predict(X_test)

#	print("Confusion Matrix: \n", sklearn.metrics.confusion_matrix(y_test, y_hat))
#	print("Precision: ", sklearn.metrics.precision_score(y_test, y_hat))
#	print("Recall: ", sklearn.metrics.recall_score(y_test, y_hat))
#	print("Accuracy: ", sklearn.metrics.accuracy_score(y_test, y_hat))

	return y_hat

MODEL_NAME = sys.argv[1]#"../models/pm_model.sav"
TFIDF_FILENAME = sys.argv[2]#"../models/tfidfmodel.sav"
tfidf = pickle.load(open(TFIDF_FILENAME, 'rb'))
model = pickle.load(open(MODEL_NAME, 'rb'))

if len(sys.argv) >= 4:
	JSON_FILENAME = sys.argv[3]
	
	# read in json file with columns title, abstract, major_mesh, minor_mesh and pm
	with open(JSON_FILENAME) as f:
			j_dict = json.load(f)
	classify(j_dict)
else:
	# In this mode, we expect one JSON document per line, classify it, return the classification value
	# and wait for the next document
	print("Waiting for input on STDIN, one JSON document batch per line")
	for line in sys.stdin:
		if line.strip() == "quit":
			sys.exit(0)
		j_dict = json.loads(line, strict=False)
		outcome = classify(j_dict)
		print("Result: " + str(outcome))
		
		


