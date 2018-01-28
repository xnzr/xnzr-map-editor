package io.xnzr.maped;

/**
 * Controller for radio source.
 */
public class RadioSourceHolder {
    public RadioSourceHolder(RadioSource radioSource, JRadioSourceView radioSourceView) {
        this.setRadioSource(radioSource);
        this.setRadioSourceView(radioSourceView);
    }

    private RadioSource radioSource;
    private JRadioSourceView radioSourceView;

    public RadioSource getRadioSource() {
        return radioSource;
    }

    public void setRadioSource(RadioSource radioSource) {
        this.radioSource = radioSource;
    }

    public JRadioSourceView getRadioSourceView() {
        return radioSourceView;
    }

    public void setRadioSourceView(JRadioSourceView radioSourceView) {
        this.radioSourceView = radioSourceView;
    }
}
