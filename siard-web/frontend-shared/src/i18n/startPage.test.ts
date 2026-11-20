import { expect, test } from "vitest";
import de from "./de.json";
import en from "./en.json";
import fr from "./fr.json";
import it from "./it.json";

const cases = [
  {
    locale: "de",
    resource: de,
    labels: ["Archivieren", "Öffnen", "Hochladen", "Exportieren"],
    descriptions: [
      "Datenbank archivieren und im SIARD-Format abspeichern.",
      "Inhalte einer SIARD-Datei anzeigen.",
      "Inhalte einer SIARD-Datei in eine Datenbank hochladen oder Daten in eine Tabelle exportieren.",
    ],
    verbs: ["archivieren", "anzeigen", "hochladen", "exportieren"],
  },
  {
    locale: "en",
    resource: en,
    labels: ["Archive", "Open", "Upload", "Export"],
    descriptions: [
      "Archive database and save in SIARD format.",
      "Display contents of a SIARD file.",
      "Upload contents of a SIARD file to a database or export data to a table.",
    ],
    verbs: ["Archive", "Display", "Upload", "export"],
  },
  {
    locale: "fr",
    resource: fr,
    labels: ["Archiver", "Ouvrir", "Téléverser", "Exporter"],
    descriptions: [
      "Archiver la base de données et la sauvegarder au format SIARD.",
      "Afficher le contenu d'un fichier SIARD.",
      "Téléverser le contenu d'un fichier SIARD dans une base de données ou exporter des données dans une table.",
    ],
    verbs: ["Archiver", "Afficher", "Téléverser", "exporter"],
  },
  {
    locale: "it",
    resource: it,
    labels: ["Archivia", "Apri", "Carica", "Esporta"],
    descriptions: [
      "Archiviare il database e salvarlo in formato SIARD.",
      "Visualizzare il contenuto di un file SIARD.",
      "Caricare il contenuto di un file SIARD in un database o esportare i dati in una tabella.",
    ],
    verbs: ["Archiviare", "Visualizzare", "Caricare", "esportare"],
  },
];

test.each(cases)(
  "$locale has complete start-page labels and emphasized descriptions",
  ({ resource, labels, descriptions, verbs }) => {
    const text = resource.startPage;
    expect(Object.keys(text).sort()).toEqual(Object.keys(en.startPage).sort());
    expect([text.archive, text.open, text.upload, text.export]).toEqual(labels);
    const rich = [
      text.archiveDescription,
      text.openDescription,
      text.destinationDescription,
    ];
    expect(rich.map((value) => value.replace(/<\/?strong>/g, ""))).toEqual(
      descriptions,
    );
    expect(
      rich.flatMap((value) =>
        [...value.matchAll(/<strong>(.*?)<\/strong>/g)].map(
          (match) => match[1],
        ),
      ),
    ).toEqual(verbs);
    expect(rich.join("")).not.toContain("�");
  },
);
