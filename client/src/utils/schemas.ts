export type Item = {
    id: string;
    title: string;
    status: string;
    price: string;
    images: string[];
};
export enum Section {
    DISCOVER= "DISCOVER",
    SEARCHPAGE="SEARCHPAGE",
    SELLING= "SELLING",
    PROFILE= "PROFILE",
    CLAIMLIST= "CLAIMLIST",
    WATCHLIST= "WATCHLIST",
    VIEW_ITEM_DETAILS= "VIEW_ITEM_DETAILS",
    POST= "POST"
}

export interface ListProps {
    section: Section;
    setSection: React.Dispatch<React.SetStateAction<Section>>
    sectionHistory: Section[];
    setSectionHistory: React.Dispatch<React.SetStateAction<Section[]>>
}
