import os
import json
import numpy as np
from tensorflow.keras.preprocessing import image


def load_dataset(directory):
    fake_articles = []
    real_articles = []

    # fake articles
    for article in os.listdir(directory+'/fake'):
        with open(directory + '/fake/' + article + '/news content.json') as json_file:
            content = json.load(json_file)
            fake_articles.append(content['text'])

    # real articles
    for article in os.listdir(directory+'/real'):
        with open(directory + '/real/' + article + '/news content.json') as json_file:
            content = json.load(json_file)
            real_articles.append(content['text'])

    x = np.concatenate((fake_articles, real_articles))

    return x


def load_images(directory, target_size):
    fake_images = []
    real_images = []

    # fake images
    for article in os.listdir(directory+'/fake'):
        image_path = directory+'/fake/'+article+'/top_img.png'
        img = image.load_img(image_path, target_size=target_size, color_mode='rgb')
        fake_images.append(image.img_to_array(img))

    # real images
    for article in os.listdir(directory+'/real'):
        image_path = directory+'/real/'+article+'/top_img.png'
        img = image.load_img(image_path, target_size=target_size, color_mode='rgb')
        real_images.append(image.img_to_array(img))

    return np.array(fake_images), np.array(real_images)


def load_labels(directory):
    y_fake = np.zeros(len(os.listdir(directory+'/fake')), dtype=int)
    y_real = np.ones(len(os.listdir(directory+'/real')), dtype=int)
    y = np.concatenate((y_fake, y_real))

    return y
