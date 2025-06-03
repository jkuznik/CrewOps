const applyDarkThemeEarly = () => {
    const html = document.documentElement;

    if (!html.hasAttribute('theme')) {
        html.setAttribute('theme', 'dark');
    }
};

document.documentElement.setAttribute('theme', 'dark');

// Dla pewności: ustaw tło na body i #outlet (działa wcześniej niż style)
document.body.style.backgroundColor = '#1e1e1e';
const outlet = document.getElementById('outlet');
if (outlet) {
    outlet.style.backgroundColor = '#1e1e1e';
}


applyDarkThemeEarly();
