import os
import logging
import requests
import random
import base64
from newspaper import Article
from PIL import Image
import numpy as np
from tensorflow.keras.preprocessing import image


def crawl_news(url):
    try:
        article = Article(url)
        article.download()
        article.parse()
    except:
        logging.exception("Exception in fetching article form URL")
        return "", ""

    text = article.text
    top_image = article.top_image

    return text, top_image


def get_image(top_image, address, from_url):
    image_size = (299, 299)
    img = None
    path = 'temp_images/' + str(address) + str(random.randint(0, 10000)) + '.png'
    try:
        if from_url:
            img_data = requests.get(top_image).content
        else:
            img_data = base64.b64decode(top_image)
        with open(path, 'wb') as handler:
            handler.write(img_data)

        # check shape
        with Image.open(path) as image_f:
            width, height = image_f.size
            if width >= 75 and height >= 75:
                img = image.load_img(path, target_size=image_size, color_mode='rgb')

    except Exception as e:
        if img is not None:
            logging.exception(e)

    # delete image
    if os.path.exists(path):
        os.remove(path)

    if img is not None:
        img = image.img_to_array(img)
        img = np.expand_dims(img, axis=0)
    return img
