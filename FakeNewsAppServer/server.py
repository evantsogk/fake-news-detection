import utils
import socket
import threading
import json
import datetime
import logging
import pickle
import numpy as np
from tensorflow.keras.applications.xception import Xception
from tensorflow.keras.applications.xception import preprocess_input
from tensorflow.keras.preprocessing.sequence import pad_sequences
from tensorflow.keras.models import Model, load_model


class Server:
    def __init__(self, host, port):
        self.host = host
        self.port = port
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.sock.bind((self.host, self.port))

    def listen(self):
        self.sock.listen()
        print("Host:", self.host, "Port:", self.port, "listening...")
        while True:
            client, address = self.sock.accept()
            timestamp = datetime.datetime.now().strftime('%d-%m-%Y %H:%M:%S')
            print(timestamp, ":", address, "connected")
            threading.Thread(target=communication, args=(client, address)).start()


def evaluate_news(text, image):
    text_input_politifact = tokenizer_politifact.texts_to_sequences([text])
    text_input_politifact = pad_sequences(text_input_politifact, padding='post', maxlen=1000)
    text_input_gossipcop = tokenizer_gossipcop.texts_to_sequences([text])
    text_input_gossipcop = pad_sequences(text_input_gossipcop, padding='post', maxlen=1000)

    if image is not None:
        image = preprocess_input(image)
        image_input = xception.predict(image)

        pred_politifact = multimodal_politifact.predict([text_input_politifact, image_input])
        pred_gossipcop = multimodal_gossipcop.predict([text_input_gossipcop, image_input])
    else:
        pred_politifact = text_politifact.predict(text_input_politifact)
        pred_gossipcop = text_gossipcop.predict(text_input_gossipcop)

    label_politifact = int(np.argmax(pred_politifact, axis=1)[0])
    score_politifact = float(pred_politifact[:, label_politifact][0])
    label_gossipcop = int(np.argmax(pred_gossipcop, axis=1)[0])
    score_gossipcop = float(pred_gossipcop[:, label_gossipcop][0])

    return label_politifact, score_politifact, label_gossipcop, score_gossipcop


def communication(client, address):
    max_size = 999999
    try:
        size = client.recv(32)
        size = int(size)

        if 2*size < max_size:
            string_news = client.recv(2 * size)
        else:
            string_news = b''
            while len(string_news) < size:
                string_news += client.recv(max_size)

        if not string_news:
            client.close()
            return

        json_news = json.loads(string_news)

        # get news image and text
        if "news_url" in json_news:
            news_text, top_image = utils.crawl_news(json_news["news_url"])
            if len(news_text) > 0:
                json_news["text"] = news_text
            news_image = utils.get_image(top_image, address, True)
            if news_image is not None:
                json_news["news_image"] = top_image
        else:
            news_text = json_news["text"]
            news_image = utils.get_image(json_news["image"], address, False)
            json_news["image"] = None  # don't send image back

        # predict
        if len(news_text) > 0:
            label_politifact, score_politifact, label_gossipcop, score_gossipcop = evaluate_news(news_text, news_image)
            json_news["evaluation"]["label_politifact"] = label_politifact
            json_news["evaluation"]["score_politifact"] = score_politifact
            json_news["evaluation"]["label_gossipcop"] = label_gossipcop
            json_news["evaluation"]["score_gossipcop"] = score_gossipcop

            string_news = json.dumps(json_news)
            string_news = string_news.encode('utf-8')

        client.sendall(string_news)
        client.close()
    except Exception as e:
        logging.exception(e)
        client.close()


if __name__ == "__main__":
    HOST = "192.168.1.100"
    PORT = 50000

    # load tokenizers
    with open('models/tokenizer_politifact.pickle', 'rb') as handle:
        tokenizer_politifact = pickle.load(handle)
    with open('models/tokenizer_gossipcop.pickle', 'rb') as handle:
        tokenizer_gossipcop = pickle.load(handle)

    # load models
    multimodal_politifact = load_model('models/multimodal_politifact.h5')
    multimodal_gossipcop = load_model('models/multimodal_gossipcop.h5')
    text_politifact = load_model('models/text_politifact.h5')
    text_gossipcop = load_model('models/text_gossipcop.h5')
    base_model = Xception(weights='imagenet')
    xception = Model(inputs=base_model.input, outputs=base_model.get_layer(base_model.layers[-2].name).output)

    # start server
    Server(HOST, PORT).listen()
