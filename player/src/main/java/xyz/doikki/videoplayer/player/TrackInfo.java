package xyz.doikki.videoplayer.player;

import java.util.ArrayList;
import java.util.List;

public class TrackInfo {
    private List<TrackInfoBean> audio;
    private List<TrackInfoBean> subtitle;

    public TrackInfo() {
        audio = new ArrayList<>();
        subtitle = new ArrayList<>();
    }

    public List<TrackInfoBean> getAudio() {
        return audio;
    }

    public int getAudioSelected(boolean track) {
        return get_elected(audio, track);
    }

    public int getSubtitleSelected(boolean track) {
        return get_elected(subtitle, track);
    }

    public int get_elected(List<TrackInfoBean> list, boolean track) {
        int i = 0;
        for (TrackInfoBean audio : list) {
            if (audio.selected) return track ? audio.index : i;
            i++;
        }
        return -1;
    }

    public void addAudio(TrackInfoBean audio) {
        this.audio.add(audio);
    }

    public List<TrackInfoBean> getSubtitle() {
        return subtitle;
    }

    public void addSubtitle(TrackInfoBean subtitle) {
        this.subtitle.add(subtitle);
    }
}
