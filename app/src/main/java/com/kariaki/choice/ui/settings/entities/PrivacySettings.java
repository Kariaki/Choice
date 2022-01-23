package com.kariaki.choice.ui.settings.entities;

public class PrivacySettings {

    private boolean allowDmFromNoneContacts;
    private int showLastSeenTo;
    private boolean searchOnlyMyContacts;

    public PrivacySettings(boolean allowDmFromNoneContacts, int showLastSeenTo, boolean searchOnlyMyContacts) {
        this.allowDmFromNoneContacts = allowDmFromNoneContacts;
        this.showLastSeenTo = showLastSeenTo;
        this.searchOnlyMyContacts = searchOnlyMyContacts;
    }

    public PrivacySettings(){

    }

    public boolean isSearchOnlyMyContacts() {
        return searchOnlyMyContacts;
    }

    public void setSearchOnlyMyContacts(boolean searchOnlyMyContacts) {
        this.searchOnlyMyContacts = searchOnlyMyContacts;
    }

    public boolean isAllowDmFromNoneContacts() {
        return allowDmFromNoneContacts;
    }

    public void setShowLastSeenTo(int showLastSeenTo) {
        this.showLastSeenTo = showLastSeenTo;
    }

    public void setAllowDmFromNoneContacts(boolean allowDmFromNoneContacts) {
        this.allowDmFromNoneContacts = allowDmFromNoneContacts;
    }

    public int getShowLastSeenTo() {
        return showLastSeenTo;
    }
}
