README File


This is a software for reading .WAV files and visualizing their wave form. 
This program first processes the header of .WAV files and stores the useful information (e.g. sample rate, bits per sample, data size) in a structure Wave. Since the bytes are in little-endian in .WAV files, the program translates them to big endian. It then reads the data samples.

Instructions:
Step 1: home page with the Browse button to choose a file.
Step 2: choose a file to read. Once the file is chosen, the window shows the name of the file on top right corner.
Step 3: the waveform is displayed in rows to accommodate all the sample values. Also the number of samples and the maximum value are displayed at the bottom of the window. 


