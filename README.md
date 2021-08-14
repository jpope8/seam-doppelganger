# Seam Carving for Image Classification Privacy

![cover](./assets/cover.png)
## Contents
- [Introduction](#introduction)
- [Overview](#overview)
- [Usage](#usage)
- [Conclusions](#conclusions)
- [References](#references)

## Introduction
> Add refernce to icrparm paper [[1]](https://pubs.rsna.org/doi/10.1148/ryct.2020200280 "A Characteristic Chest Radiographic Pattern in the Setting of the COVID-19 Pandemic").<br>

## Overview
> Seam Carving for Image Classification Privacy
> Welcome to the seam-doppelganger wiki!  This page gives an overview of an approach for making an image harder to classify using machine learning techniques using seam carving while remaining human recognisable.  We first briefly explain seam carving and then the modified approach termed seam doppelganger.
> Seam carving is an image compression technique that finds a contiguous sequence of pixels (from top to bottom or left to right), termed a _seam_, to remove from an image.  By removing just the seam, the dimension of the image is simply decreased by one.  There can be several approaches to finding a seam, however, typically the seam that is considered the most redundant (i.e. provides the least information) is removed.  The process can be repeated multiple times, both horizontally and vertically, resulting in a reduced image that retains most of the information, because by design, less informative pixels have been removed.  Seam carving is also termed _content-aware image resizing_ as it attempts to automatically find important objects in the image and retain them during resizing.  The seam carving approach can be found in the [ACM Digital Library](https://dl.acm.org/doi/10.1145/1275808.1276390).
> Seam doppelganger modifies seam carving by instead replacing the seam with the goal of making it harder to classify the image using machine learning (e.g. convolutional neural networks).  Typically, the replacing seam has a pattern unrelated to the image and it is desirable that the original seam can be approximated using the replacing seam.  This would allow the original image to be roughly restored.  As more seams are replaced, the image size remains the same but the image becomes harder to recognise.  Ideally machine learning classifiers fail before humans recognition fails.

* ___Note:___ The dataset and the trained models can be found in [here](https://drive.google.com/drive/folders/14L8wd-d2a3lvgqQtwV-y53Gsnn6Ud2-w?usp=sharing).<br>


## Usage
> - Clone the repository
> ```bash
> git clone 'https://github.com/priyavrat-misra/xrays-and-gradcam.git' && cd xrays-and-gradcam/
> ```
> - Install dependencies
> ```bash
> pip install -r requirements.txt
> ```
> - Using `argparse` script for inference
> ```bash
> python overlay_cam.py --help
> ```
> ```
> usage: GradCAM on Chest X-Rays [-h] [-i IMAGE_PATH]
>                                [-l {covid_19,lung_opacity,normal,pneumonia}]
>                                -m {vgg16,resnet18,densenet121}
>                                [-o OUTPUT_PATH]
> 
> Overlays given label's CAM on a given Chest X-Ray.
> 
> optional arguments:
>   -h, --help            show this help message and exit
>   -i IMAGE_PATH, --image-path IMAGE_PATH
>                         Path to chest X-Ray image.
>   -l {covid_19,lung_opacity,normal,pneumonia}, --label {covid_19,lung_opacity,normal,pneumonia}
>                         Choose from covid_19, lung_opacity, normal &
>                         pneumonia, to get the corresponding CAM. If not
>                         mentioned, the highest scoring label is considered.
>   -m {vgg16,resnet18,densenet121}, --model {vgg16,resnet18,densenet121}
>                         Choose from vgg16, resnet18 or densenet121.
>   -o OUTPUT_PATH, --output-path OUTPUT_PATH
>                         Format: "<path> + <file_name> + .jpg"
> ```
> - An example
> ```bash
> python overlay_cam.py --image-path ./assets/original.jpg --label covid_19 --model resnet18 --output-path ./assets/dense_cam.jpg
> ```
> ```
> GradCAM generated for label "covid_19".
> GradCAM masked image saved to "./assets/res_cam.jpg".
> ```

## Conclusions
> - DenseNet-121 having only `7.98 Million` parameters did relatively better than VGG-16 and ResNet-18, with `138 Million` and `11.17 Million` parameters respectively.
> - Increase in model's parameter count doesn’t necessarily achieve better results, but increase in residual connections might.
> - Oversampling helped in dealing with imbalanced data to a great extent.
> - Fine-tuning helped substantially by dealing with the comparatively small dataset and speeding up the training process.
> - GradCAM aided in localizing the areas in CXRs that decides a model's predictions.
> - The models did a good job distinguishing various infectious and inflammatory lung diseases, which is rather hard manually, as mentioned earlier.

## References
> - [1] David L. Smith, John-Paul Grenier, Catherine Batte, and Bradley Spieler. [A Characteristic Chest Radiographic Pattern in the Setting of the COVID-19 Pandemic](https://pubs.rsna.org/doi/10.1148/ryct.2020200280). Radiology: Cardiothoracic Imaging 2020 2:5.
> - [2] Hyun Jung Koo, Soyeoun Lim, Jooae Choe, Sang-Ho Choi, Heungsup Sung, and Kyung-Hyun Do. [Radiographic and CT Features of Viral Pneumonia](https://pubs.rsna.org/doi/10.1148/rg.2018170048). RadioGraphics 2018 38:3, 719-739.
> - [3] Tawsifur Rahman, Muhammad Chowdhury, Amith Khandakar. [COVID-19 Radiography Database](https://www.kaggle.com/tawsifurrahman/covid19-radiography-database). Kaggle.
> - [4] Karen Simonyan, Andrew Zisserman. [Very Deep Convolutional Networks for Large-Scale Image Recognition](https://arxiv.org/abs/1409.1556). arxiv:1409.1556v6.
> - [5] Kaiming He, Xiangyu Zhang, Shaoqing Ren, Jian Sun. [Deep Residual Learning for Image Recognition](https://arxiv.org/abs/1512.03385). arxiv:1512.03385v1.
> - [6] Gao Huang, Zhuang Liu, Laurens van der Maaten, Kilian Q. Weinberger. [Densely Connected Convolutional Networks](https://arxiv.org/abs/1608.06993). arxiv:1608.06993v5.
> - [7] Deng, J. et al., 2009. [Imagenet: A large-scale hierarchical image database. In 2009 IEEE conference on computer vision and pattern recognition](http://image-net.org/). pp. 248–255.
> - [8] Ramprasaath R. Selvaraju, Michael Cogswell, Abhishek Das, Ramakrishna Vedantam, Devi Parikh, Dhruv Batra. [Grad-CAM: Visual Explanations from Deep Networks via Gradient-based Localization](https://arxiv.org/abs/1610.02391). arXiv:1610.02391v4.

