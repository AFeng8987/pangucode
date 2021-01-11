package org.linlinjava.litemall.core.express;

import org.junit.Test;

import java.io.File;

public class FileUpdateTest {

    @Test
    public  void testFileUpdate(String[] args) {
        File source = new File("C:\\Users\\11985\\Desktop\\a\\4.mp4");
        File target = new File("C:\\Users\\11985\\Desktop\\a\\44.mp4");
        /*try {
            AudioAttributes audio = new AudioAttributes();
            audio.setCodec("libmp3lame");
            audio.setBitRate(new Integer(56000));
            audio.setChannels(new Integer(1));
            audio.setSamplingRate(new Integer(22050));
            VideoAttributes video = new VideoAttributes();
            video.setCodec("mpeg4");
            video.setBitRate(new Integer(800000));
            video.setFrameRate(new Integer(15));
            EncodingAttributes attrs = new EncodingAttributes();
            attrs.setFormat("mp4");
            attrs.setAudioAttributes(audio);
            attrs.setVideoAttributes(video);
            Encoder encoder = new Encoder();
            encoder.encode(source, target, attrs);
        } catch (EncoderException e) {
            e.printStackTrace();
        }*/
    }
}
