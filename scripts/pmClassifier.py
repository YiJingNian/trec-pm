import sys
import json
import pandas as pd
import pickle
import string
from nltk.stem import PorterStemmer
from nltk import word_tokenize
from sklearn.pipeline import Pipeline
from sklearn.feature_extraction.text import TfidfVectorizer
from nltk.corpus import stopwords
import sklearn
from sklearn import preprocessing

def stemming_tokenizer(text):
    stemmer = PorterStemmer()
    return [stemmer.stem(w) for w in word_tokenize(text)]

JSON_FILENAME = sys.argv[1]
TFIDF_FILENAME = "../models/tfidfmodel.sav"
MODEL_NAME = "../models/pm_model.sav"

# read in json file with columns title, abstract, major_mesh, minor_mesh and pm
with open(JSON_FILENAME) as f:
    j_dict = json.load(f)

# adds title, abstracts and mesh terms into one document
X_test = pd.Series(
    [j_dict[str(i)]["title"] + " " + j_dict[str(i)]["abstract"] +
    " " + str(j_dict[str(i)]["major_mesh"] or "") + " " + str(j_dict[str(i)]["minor_mesh"] or "")
    for i in range(len(j_dict))]
)

tfidf = pickle.load(open(TFIDF_FILENAME, 'rb'))
X_test = tfidf.transform(X_test)

# pm should be 1 for either human or animal pm or 0 for non pm
y_test = pd.Series([j_dict[str(i)]["pm"] for i in range(len(j_dict))])
#Encode from string to numbers
enc = preprocessing.LabelEncoder()
y_test = enc.fit_transform(y_test)
model = pickle.load(open(MODEL_NAME, 'rb'))
y_hat = model.predict(X_test)

print("Confusion Matrix: \n", sklearn.metrics.confusion_matrix(y_test, y_hat))
print("Precision: ", sklearn.metrics.precision_score(y_test, y_hat))
print("Recall: ", sklearn.metrics.recall_score(y_test, y_hat))
print("Accuracy: ", sklearn.metrics.accuracy_score(y_test, y_hat))
