<h1 align="center">
  <a href="https://github.com/jpope8/seam-doppelganger">
    <!-- Please provide path to your logo here -->
    <img src="docs/images/jacamar_seam.png" alt="Logo" width="100" height="100">
  </a>
</h1>

<div align="center">
  Seam Doppelganger
  <br />
  <a href="#usage"><strong>Usage »</strong></a>
  <br />
  <!--
  <br />
  <a href="https://github.com/jpope8/seam-doppelganger/issues/new?assignees=&labels=bug&template=01_BUG_REPORT.md&title=bug%3A+">Report a Bug</a>
  ·
  <a href="https://github.com/jpope8/seam-doppelganger/issues/new?assignees=&labels=enhancement&template=02_FEATURE_REQUEST.md&title=feat%3A+">Request a Feature</a>
  .
  <a href="https://github.com/jpope8/seam-doppelganger/issues/new?assignees=&labels=question&template=04_SUPPORT_QUESTION.md&title=support%3A+">Ask a Question</a>
  -->
</div>

<div align="center">
<br />

[![license](https://img.shields.io/github/license/jpope8/seam-doppelganger.svg?style=flat-square)](LICENSE)

[![PRs welcome](https://img.shields.io/badge/PRs-welcome-ff69b4.svg?style=flat-square)](https://github.com/jpope8/seam-doppelganger/issues?q=is%3Aissue+is%3Aopen+label%3A%22help+wanted%22)
[![code with hearth by jpope8](https://img.shields.io/badge/%3C%2F%3E%20with%20%E2%99%A5%20by-jpope8-ff1414.svg?style=flat-square)](https://github.com/jpope8)

</div>

## Contents

- [Introduction](#introduction)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
- [Usage](#usage)
- [Contributing](#contributing)
- [Authors & contributors](#authors--contributors)
- [Conclusions](#conclusions)
- [Acknowledgements](#acknowledgements)
- [References](#references)
- [License](#license)


---


## Introduction
Seam Carving for Image Classification Privacy

Welcome to the seam-doppelganger wiki!  This page gives an overview of an approach for making an image harder to classify using machine learning techniques using seam carving while remaining human recognisable.  The overview briefly explains seam carving and then the modified approach termed seam doppelganger.<br>

Seam carving is an image compression technique that finds a contiguous sequence of pixels (from top to bottom or left to right), termed a _seam_, to remove from an image.  By removing just the seam, the dimension of the image is simply decreased by one.  There can be several approaches to finding a seam, however, typically the seam that is considered the most redundant (i.e. provides the least information) is removed.  The process can be repeated multiple times, both horizontally and vertically, resulting in a reduced image that retains most of the information, because by design, less informative pixels have been removed.  Seam carving is also termed _content-aware image resizing_ as it attempts to automatically find important objects in the image and retain them during resizing.  The seam carving approach can be found in the [ACM Digital Library](https://dl.acm.org/doi/10.1145/1275808.1276390).<br>

Seam doppelganger modifies seam carving by instead replacing the seam with the goal of making it harder to classify the image using machine learning (e.g. convolutional neural networks).  Typically, the replacing seam has a pattern unrelated to the image and it is desirable that the original seam can be approximated using the replacing seam.  This would allow the original image to be roughly restored.  As more seams are replaced, the image size remains the same but the image becomes harder to recognise.  Ideally machine learning classifiers fail before humans recognition fails.<br>

This work was initially presented at ICPRAM 2021 [[1]](https://www.scitepress.org/PublicationsDetail.aspx?ID=H8zqc3KCMlw=&t=1 "Seam Carving for Image Classification Privacy").<br>

* ___Note:___ The images used in the paper are from Imagenet which can be accessedd [here](https://www.image-net.org/).<br>


## Usage

To process one image and view.

> - Clone the repository
> ```bash
> git clone 'https://github.com/jpope8/seam-doppelganger.git' && cd seam-doppelganger/
> ```
> - Install dependencies
> Any version of the Java Development Kit (JDK) later than 1.8
> Any version of Python3
> ```bash
> pip install -r requirements.txt
> ```
> - An example seam doppelganger for specified percentage replacement
> ```bash
> java ReplaceDemo ../images/jacamar.jpg 0.25
> ls ../images
> jacamar.jpg  jacamar\_rand.png  jacamar\_seam.png
> ```
> - An example showing the intermediate energy image and one horizontal and vertical seams identified.
> ```bash
> java ShowSeams ../images/jacamar.jpg
> ```

<details>
<summary>Screenshots</summary>
<br>

> **[?]**
> Please provide your screenshots here.

|                          Original Image                               |                       Seam Dopplegange Image                           |
| :-------------------------------------------------------------------: | :--------------------------------------------------------------------: |
| <img src="docs/images/jacamar.jpg" title="Original" width="75%"> | <img src="docs/images/jacamar_seam.png" title="Seam Doppelgange" width="75%"> |

</details>


## Advanced Usage

To process multiple images, first

> - Clone the repository
> ```bash
> git clone 'https://github.com/priyavrat-misra/xrays-and-gradcam.git' && cd xrays-and-gradcam/
> ```
> - Install dependencies
> ```bash
> pip install -r requirements.txt
> ```
> - An example seam doppelganger for specified percentage replacement
> ```bash
> java PaperDemo ../images/bird/ ../images/bird_05/ 0.05
> java PaperDemo ../images/bird/ ../images/bird_10/ 0.10
> java PaperDemo ../images/bird/ ../images/bird_15/ 0.15
> java PaperDemo ../images/bird/ ../images/bird_20/ 0.20
> java PaperDemo ../images/bird/ ../images/bird_25/ 0.25
> ```
> - An example using resnet to classify images
> ```bash
> python resnetPaper.py
> python resnetTikz.py myOutFile.txt
> ```



## Conclusions
> - Increase in model's parameter count doesn’t necessarily achieve better results, but increase in residual connections might.
> - Oversampling helped in dealing with imbalanced data to a great extent.
> - Fine-tuning helped substantially by dealing with the comparatively small dataset and speeding up the training process.
> - GradCAM aided in localizing the areas in CXRs that decides a model's predictions.
> - The models did a good job distinguishing various infectious and inflammatory lung diseases, which is rather hard manually, as mentioned earlier.

## Citing

Please cite the following paper.

    @conference{icpram21,
      author={James Pope. and Mark Terwilliger.},
      title={Seam Carving for Image Classification Privacy},
      booktitle={Proceedings of the 10th International Conference on Pattern Recognition Applications and Methods - ICPRAM,},
      year={2021},
      pages={268-274},
      publisher={SciTePress},
      organization={INSTICC},
      doi={10.5220/0010249702680274},
      isbn={978-989-758-486-2},
      issn={2184-4313},
    }

## References
> - [1] David L. Smith, John-Paul Grenier, Catherine Batte, and Bradley Spieler. [A Characteristic Chest Radiographic Pattern in the Setting of the COVID-19 Pandemic](https://pubs.rsna.org/doi/10.1148/ryct.2020200280). Radiology: Cardiothoracic Imaging 2020 2:5.
> - [2] Avidan, Shai and Shamir, Ariel. [Seam Carving for Content-Aware Image Resizing](https://dl.acm.org/doi/10.1145/1276377.1276390). ACM Transactions on Graphics, Volume 26, Issue 3, July 2007. 
    @article{SEAM2007,
        author = {Avidan, Shai and Shamir, Ariel},
        title = {Seam Carving for Content-Aware Image Resizing},
        year = {2007},
        issue_date = {July 2007},
        publisher = {Association for Computing Machinery},
        address = {New York, NY, USA},
        volume = {26},
        number = {3},
        issn = {0730-0301},
        url = {https://doi.org/10.1145/1276377.1276390},
        doi = {10.1145/1276377.1276390},
        journal = {ACM Trans. Graph.},
        month = jul,
        pages = {10–es},
        numpages = {10},
        keywords = {image retargeting, image resizing, image seams, display devices, content-aware image manipulation}
    }
> - [3] Tawsifur Rahman, Muhammad Chowdhury, Amith Khandakar. [COVID-19 Radiography Database](https://www.kaggle.com/tawsifurrahman/covid19-radiography-database). Kaggle.



## License

This project is licensed under the **Apache Software License 2.0**.

See [LICENSE](LICENSE) for more information.

