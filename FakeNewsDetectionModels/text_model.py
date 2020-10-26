import data
import numpy as np
from sklearn.model_selection import train_test_split
from sklearn.metrics import precision_recall_fscore_support, accuracy_score
from tensorflow import keras
import time


def train(dataset):
    # load dataset
    news = data.load_dataset(dataset)
    y = data.load_labels(dataset)

    # train-test split
    news_train, news_test, y_train, y_test = train_test_split(news, y, test_size=0.2, random_state=0, stratify=y)
    y_train = keras.utils.to_categorical(y_train)

    # Text preparation
    EMBEDDINGS_FILE = "D:/Users/Vagelis/PycharmProjects/embeddings/fastText/crawl-300d-2M.vec/crawl-300d-2M.vec"
    MAX_SEQUENCE_LENGTH = 1000
    EMBEDDING_DIM = 300

    tokenizer = keras.preprocessing.text.Tokenizer()
    tokenizer.fit_on_texts(news_train)
    X_train = tokenizer.texts_to_sequences(news_train)
    X_test = tokenizer.texts_to_sequences(news_test)
    vocab_size = len(tokenizer.word_index) + 1

    X_train = keras.preprocessing.sequence.pad_sequences(X_train, padding='post', maxlen=MAX_SEQUENCE_LENGTH)
    X_test = keras.preprocessing.sequence.pad_sequences(X_test, padding='post', maxlen=MAX_SEQUENCE_LENGTH)

    # embedding matrix
    embedding_matrix = np.zeros((vocab_size, EMBEDDING_DIM))

    with open(EMBEDDINGS_FILE, encoding="utf8") as file:
        for line in file:
            word, *vector = line.split()
            if word in tokenizer.word_index:
                idx = tokenizer.word_index[word]
                embedding_matrix[idx] = np.array(vector, dtype=np.float32)[:EMBEDDING_DIM]

    # Model
    model = keras.Sequential()
    model.add(keras.layers.Embedding(input_dim=vocab_size, output_dim=EMBEDDING_DIM, weights=[embedding_matrix],
                                     input_length=MAX_SEQUENCE_LENGTH, trainable=False))
    model.add(keras.layers.Dropout(0.2))

    model.add(keras.layers.Conv1D(filters=256, kernel_size=3, activation='relu'))
    model.add(keras.layers.GlobalMaxPooling1D())

    model.add(keras.layers.Dense(128, activation='relu', kernel_regularizer=keras.regularizers.l2(0.0001)))
    model.add(keras.layers.Dropout(0.5))
    model.add(keras.layers.Dense(2, activation='softmax'))

    # Train and predict
    model.compile(optimizer=keras.optimizers.Adam(learning_rate=0.001), loss='categorical_crossentropy',
                  metrics=['categorical_accuracy'])
    early_stopping = keras.callbacks.EarlyStopping(monitor='val_loss', patience=5, restore_best_weights=True)

    start = time.time()
    model.fit(X_train, y_train, epochs=500, batch_size=32, verbose=0, validation_split=0.1, callbacks=[early_stopping])
    end = time.time()
    print("Time:", (end - start) / 60)

    y_pred = np.argmax(model.predict(X_test), axis=1)
    acc = accuracy_score(y_test, y_pred)
    print('Accuracy:', acc)
    prf = precision_recall_fscore_support(y_test, y_pred)
    print(prf)

    return model, tokenizer
