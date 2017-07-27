#pragma once

#include <vector>

#include "opencv2/imgcodecs.hpp"
#include "opencv2/highgui/highgui.hpp"
#include "opencv2/imgproc/imgproc.hpp"

using namespace std;
using namespace cv;

void ColorHistogramEqualization(Mat &in_image, Mat&out_image);
void PreprocessImage(Mat &in_image, Mat&out_image);
void HoughCircleDetection(const Mat& src_gray, const Mat& src_display, Mat &out_image);
int  ProcessImage(const string &in_file, const string &out_file);

void ColorHistogramEqualization(Mat &in_image, Mat&out_image)
{
	Mat yuv_image;
	vector<Mat> yuv_channels;
	cvtColor(in_image, yuv_image, COLOR_BGR2YUV);

	split(yuv_image, yuv_channels);

	equalizeHist(yuv_channels[0], yuv_channels[0]);

	merge(yuv_channels, out_image);

	cvtColor(out_image, out_image, COLOR_YUV2BGR);
}

void PreprocessImage(Mat &in_image, Mat&out_image)
{
	cvtColor(in_image, out_image, COLOR_BGR2GRAY);

	GaussianBlur(out_image, out_image, Size(9, 9), 2, 2);
}

void HoughCircleDetection(const Mat& src_gray,
	const Mat& src_display,
	Mat &out_image)
{
	std::vector<Vec3f> circles;

	HoughCircles(src_gray, circles,
		HOUGH_GRADIENT, 1, src_gray.rows / 8,
		200, 50, 0, 0);

	out_image = src_display.clone();
	for (size_t i = 0; i < circles.size(); i++)
	{
		Point center(cvRound(circles[i][0]), cvRound(circles[i][1]));
		int radius = cvRound(circles[i][2]);
		circle(out_image, center, 3, Scalar(0, 255, 0), -1, 8, 0);
		circle(out_image, center, radius, Scalar(0, 0, 255), 3, 8, 0);
	}
}

int ProcessImage(const string &in_file, const string &out_file)
{
	Mat src, hist_eq, pre_proc, dest;

	src = imread(in_file);

	if (src.empty())
	{
		return -1;
	}

	ColorHistogramEqualization(src, hist_eq);

	PreprocessImage(hist_eq, pre_proc);

	HoughCircleDetection(pre_proc, src, dest);

	imwrite(out_file, dest);

	return 0;
}