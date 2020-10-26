import data
import numpy as np
import time
from sklearn.model_selection import train_test_split
from sklearn.metrics import precision_recall_fscore_support, accuracy_score
from tensorflow.keras.utils import to_categorical
from tensorflow.keras.preprocessing.text import Tokenizer
from tensorflow.keras.preprocessing.sequence import pad_sequences
from tensorflow.keras.regularizers import l2
from tensorflow.keras.models import Model
from tensorflow.keras.layers import Embedding, Dropout, Conv1D, GlobalMaxPooling1D, Dense, Input, concatenate
from tensorflow.keras.optimizers import Adam
from tensorflow.keras.callbacks import EarlyStopping


def train(dataset):
    # load dataset
    news = data.load_dataset(dataset)
    y = data.load_labels(dataset)

    # load image features
    images = np.load(dataset + '/xception.npy')

    # train-test split
    news_train, news_test, y_train, y_test = train_test_split(news, y, test_size=0.2, random_state=0, stratify=y)
    images_train, images_test, _, _ = train_test_split(images, y, test_size=0.2, random_state=0, stratify=y)
    y_train = to_categorical(y_train)

    # Text preparation
    EMBEDDINGS_FILE = "D:/Users/Vagelis/PycharmProjects/embeddings/fastText/crawl-300d-2M.vec/crawl-300d-2M.vec"
    MAX_SEQUENCE_LENGTH = 1000
    EMBEDDING_DIM = 300

    tokenizer = Tokenizer()
    tokenizer.fit_on_texts(news_train)
    text_train = tokenizer.texts_to_sequences(news_train)
    text_test = tokenizer.texts_to_sequences(news_test)

    vocab_size = len(tokenizer.word_index) + 1

    text_train = pad_sequences(text_train, padding='post', maxlen=MAX_SEQUENCE_LENGTH)
    text_test = pad_sequences(text_test, padding='post', maxlen=MAX_SEQUENCE_LENGTH)

    # embedding matrix
    embedding_matrix = np.zeros((vocab_size, EMBEDDING_DIM))
    with open(EMBEDDINGS_FILE, encoding="utf8") as file:
        for line in file:
            word, *vector = line.split()
            if word in tokenizer.word_index:
                idx = tokenizer.word_index[word]
                embedding_matrix[idx] = np.array(vector, dtype=np.float32)[:EMBEDDING_DIM]

    # Text Model
    input_text = Input(MAX_SEQUENCE_LENGTH, )
    embedding = Embedding(input_dim=vocab_size, output_dim=EMBEDDING_DIM, weights=[embedding_matrix],
                          input_length=MAX_SEQUENCE_LENGTH, trainable=False)(input_text)
    dropout_1 = Dropout(0.2)(embedding)
    conv1D = Conv1D(filters=256, kernel_size=3, activation='relu')(dropout_1)
    global_max_pooling1d = GlobalMaxPooling1D()(conv1D)
    dense_1 = Dense(128, activation='relu', kernel_regularizer=l2(0.0001))(global_max_pooling1d)
    dropout_2 = Dropout(0.5)(dense_1)

    # Visual model
    input_visual = Input(images.shape[1], )
    dense_2 = Dense(32, activation='relu')(input_visual)
    dropout_3 = Dropout(0.5)(dense_2)

    # Concatenation
    merge = concatenate([dropout_2, dropout_3])
    dense_3 = Dense(64, activation='relu')(merge)
    dropout_4 = Dropout(0.5)(dense_3)
    output = Dense(2, activation='softmax')(dropout_4)

    # Train and predict
    model = Model(inputs=[input_text, input_visual], outputs=[output])
    model.compile(optimizer=Adam(learning_rate=0.001), loss='categorical_crossentropy',
                  metrics=['categorical_accuracy'])
    early_stopping = EarlyStopping(monitor='val_loss', patience=5, restore_best_weights=True)

    start = time.time()
    model.fit(x=[text_train, images_train], y=[y_train], epochs=100, batch_size=32, verbose=0,  validation_split=0.1,
              callbacks=[early_stopping])
    end = time.time()
    print()
    print("Time:", (end - start) / 60)

    pred = model.predict(x=[text_test, images_test])
    y_pred = np.argmax(pred, axis=1)

    acc = accuracy_score(y_test, y_pred)
    print('Accuracy:', acc)
    prf = precision_recall_fscore_support(y_test, y_pred)
    print(prf)

    return model, tokenizer
