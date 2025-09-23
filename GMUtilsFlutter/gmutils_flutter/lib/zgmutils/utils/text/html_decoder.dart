class HtmlDecoder {
//https://www.w3schools.com/html/html_entities.asp

  String decode(String text) {
    if (text.startsWith('<br>')) {
      text = text.replaceFirst('<br>', '');
    }
    return text
        .replaceAll('&nbsp;', ' ')
        .replaceAll('&lt;', '<')
        .replaceAll('&gt;', '>')
        .replaceAll('&amp;', '&')
        .replaceAll('&quot;', '"')
        .replaceAll('&apos;', '\'')
        .replaceAll('&cent;', '¢')
        .replaceAll('&pound;', '£')
        .replaceAll('&yen;', '¥')
        .replaceAll('&euro;', '€')
        .replaceAll('&copy;', '©')
        .replaceAll('&reg;', '®')
        .replaceAll('&forall;', '∀')
        .replaceAll('&part;', '∂')
        .replaceAll('&exist;', '∃')
        .replaceAll('&empty;', '∅')
        .replaceAll('&nabla;', '∇')
        .replaceAll('&isin;', '∈')
        .replaceAll('&notin;', '∉')
        .replaceAll('&ni;', '∋')
        .replaceAll('&prod;', '∏')
        .replaceAll('&sum;', '∑')
        .replaceAll('<p>', '\n')
        .replaceAll('</p>', '\n')
        .replaceAll('<hr>', '\n')
        .replaceAll('<br/>', '\n')
        .replaceAll('<br>', '\n')
        .replaceAll('<b>', '')
        .replaceAll('</b>', '')
        .replaceAll('<i>', '')
        .replaceAll('</i>', '')
        .replaceAll('<strong>', '')
        .replaceAll('</strong>', '')
        .replaceAll('<em>', '')
        .replaceAll('</em>', '')
        ;
  }
}